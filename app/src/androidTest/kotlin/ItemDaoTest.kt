package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {

    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase

    // Datos de prueba: manzanas y bananas
    private var item1 = Item(1, "Apples", 10.0, 20)
    private var item2 = Item(2, "Bananas", 15.0, 97)

    /**
     * Configuración inicial: se ejecuta antes de cada prueba (@Test).
     * Crea una base de datos temporal en la memoria RAM.
     */
    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Usamos inMemoryDatabaseBuilder para que los datos se borren al terminar la prueba
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            // Permitimos consultas en el hilo principal solo para facilitar el testing
            .allowMainThreadQueries()
            .build()
        itemDao = inventoryDatabase.itemDao()
    }

    /**
     * Limpieza: se ejecuta después de cada prueba (@After).
     * Cierra la base de datos para liberar recursos.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        inventoryDatabase.close()
    }

    // --- Funciones de utilidad para ayudar en las pruebas ---

    private suspend fun addOneItemToDb() {
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(item1)
        itemDao.insert(item2)
    }

    // --- Pruebas unitarias (@Test) ---

    /**
     * Prueba que verifica si insertar un item funciona correctamente.
     */
    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()
        val allItems = itemDao.getAllItems().first()
        // Comparamos que el primer elemento guardado sea igual al original
        assertEquals(allItems[0], item1)
    }

    /**
     * Prueba que verifica que getAllItems devuelva todos los registros de la DB.
     */
    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = itemDao.getAllItems().first()
        // Verificamos que ambos elementos coincidan en orden y contenido
        assertEquals(allItems[0], item1)
        assertEquals(allItems[1], item2)
    }
}