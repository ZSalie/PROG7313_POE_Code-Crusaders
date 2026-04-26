package com.example.budjet

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budjet.data.AppDatabase
import com.example.budjet.data.BudJetDao
import com.example.budjet.data.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: BudJetDao

    @Before
    fun setup() {

        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = db.budJetDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertUser_thenLoginWorks() = runBlocking {

        // Insert a test user into Room database
        dao.insertUser(
            User(
                username = "john",
                email = "john@gmail.com",
                password = "1234"
            )
        )

        // Attempt login using same details
        val user = dao.login("john", "1234")

        // Test passes if user is found
        assertNotNull(user)
    }
}