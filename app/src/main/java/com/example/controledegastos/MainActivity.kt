package com.example.controledegastos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.controledegastos.ui.theme.ControleDeGastosTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControleDeGastosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TelaPrincipal()
                }
            }
        }
    }
}

@Composable
fun TelaPrincipal() {
    var telaAtual by remember { mutableStateOf("cadastro") }
    var gastoEditando by remember { mutableStateOf<Gasto?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Primeira linha de botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                telaAtual = "cadastro"
                gastoEditando = null
            }) { Text("Cadastrar Gasto") }

            Button(onClick = { telaAtual = "lista" }) { Text("Ver Gastos") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Segunda linha de botões
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { telaAtual = "limite" }) { Text("Definir Limite") }

            Button(onClick = { telaAtual = "consultaLimites" }) { Text("Consultar Limites") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (telaAtual) {
            "cadastro" -> FormularioGasto(gastoEditando) { gastoEditando = null }
            "lista" -> ListaGastos(
                onEditar = {
                    gastoEditando = it
                    telaAtual = "cadastro"
                }
            )
            "limite" -> DefinirLimite()
            "consultaLimites" -> ConsultaLimites()
        }
    }
}

@Composable
fun ListaGastos(onEditar: (Gasto) -> Unit) {
    var lista by remember { mutableStateOf(emptyList<Gasto>()) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun carregarGastos() {
        scope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            lista = db.gastoDao().listarTodos()
        }
    }

    LaunchedEffect(Unit) {
        carregarGastos()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Gastos Registrados", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lista) { gasto ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Descrição: ${gasto.descricao}")
                        Text("Valor: R$ %.2f".format(gasto.valor))
                        Text("Categoria: ${gasto.categoria}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onEditar(gasto) }) {
                                Text("Editar")
                            }
                            Button(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        val db = AppDatabase.getDatabase(context)
                                        db.gastoDao().excluir(gasto)
                                        carregarGastos()
                                        launch(Dispatchers.Main) {
                                            Toast.makeText(context, "Gasto excluído com sucesso!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Excluir", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultaLimites() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var lista by remember { mutableStateOf(emptyList<Limite>()) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            lista = db.limiteDao().listarTodos()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Limites Cadastrados", style = MaterialTheme.typography.headlineSmall)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lista) { limite ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Categoria: ${limite.categoria}")
                        Text("Limite Categoria: R$ %.2f".format(limite.limiteCategoria))
                        Text("Limite Mensal: R$ %.2f".format(limite.limiteMensal))
                    }
                }
            }
        }
    }
}

@Composable
fun DefinirLimite() {
    var categoria by remember { mutableStateOf("") }
    var limiteCategoria by remember { mutableStateOf("") }
    var limiteMensal by remember { mutableStateOf("") }
    val categoriasPredefinidas = listOf("Alimentação", "Transporte", "Educação", "Lazer", "Mercado", "Contas")
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoriasPredefinidas.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            categoria = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = limiteCategoria,
            onValueChange = { limiteCategoria = it },
            label = { Text("Limite por Categoria (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = limiteMensal,
            onValueChange = { limiteMensal = it },
            label = { Text("Limite Mensal (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            val limCat = limiteCategoria.toDoubleOrNull()
            val limMen = limiteMensal.toDoubleOrNull()

            if (categoria.isBlank() || limCat == null || limMen == null) {
                Toast.makeText(context, "Preencha todos os campos corretamente!", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val limite = Limite(
                categoria = categoria,
                limiteCategoria = limCat,
                limiteMensal = limMen
            )

            scope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(context)
                db.limiteDao().definirLimite(limite)

                launch(Dispatchers.Main) {
                    Toast.makeText(context, "Limite salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    categoria = ""
                    limiteCategoria = ""
                    limiteMensal = ""
                }
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Salvar Limite")
        }
    }
}

@Composable
fun FormularioGasto(
    gastoEmEdicao: Gasto?,
    onFinalizarEdicao: () -> Unit
) {
    var descricao by remember { mutableStateOf(gastoEmEdicao?.descricao ?: "") }
    var valor by remember { mutableStateOf(gastoEmEdicao?.valor?.toString() ?: "") }
    var categoria by remember { mutableStateOf(gastoEmEdicao?.categoria ?: "") }
    val categoriasPredefinidas = listOf("Alimentação", "Transporte", "Educação", "Lazer", "Mercado", "Contas")
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val estaEditando = gastoEmEdicao != null

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição do Gasto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoria") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categoriasPredefinidas.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            categoria = cat
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val valorDouble = valor.toDoubleOrNull() ?: 0.0
                val gasto = Gasto(
                    id = gastoEmEdicao?.id ?: 0,
                    descricao = descricao,
                    valor = valorDouble,
                    categoria = categoria
                )

                scope.launch(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(context)

                    if (!estaEditando) {
                        val limite = db.limiteDao().buscarPorCategoria(categoria)
                        val totalCategoria = db.gastoDao().totalPorCategoria(categoria) ?: 0.0
                        val totalMensal = db.gastoDao().totalMensal() ?: 0.0

                        if (limite != null) {
                            if (totalCategoria + valorDouble > limite.limiteCategoria) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Limite da categoria excedido!", Toast.LENGTH_LONG).show()
                                }
                                return@launch
                            }
                            if (totalMensal + valorDouble > limite.limiteMensal) {
                                launch(Dispatchers.Main) {
                                    Toast.makeText(context, "Limite mensal excedido!", Toast.LENGTH_LONG).show()
                                }
                                return@launch
                            }
                        }

                        db.gastoDao().inserir(gasto)
                    } else {
                        db.gastoDao().atualizar(gasto)
                    }

                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            if (estaEditando) "Gasto atualizado!" else "Gasto salvo!",
                            Toast.LENGTH_SHORT
                        ).show()
                        descricao = ""
                        valor = ""
                        categoria = ""
                        onFinalizarEdicao()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estaEditando) "Atualizar Gasto" else "Salvar Gasto")
        }
    }
}
