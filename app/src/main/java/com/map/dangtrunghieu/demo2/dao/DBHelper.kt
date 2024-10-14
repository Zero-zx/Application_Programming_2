package com.map.dangtrunghieu.demo2.dao

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.map.dangtrunghieu.demo2.model.CatInOut
import com.map.dangtrunghieu.demo2.model.Category
import com.map.dangtrunghieu.demo2.model.InOut
import com.map.dangtrunghieu.demo2.model.Transaction

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "demo2.db"

        //Table Name
        const val TABLE_CATEGORY = "tblCategory"
        const val TABLE_INOUT = "tblInOut"
        const val TABLE_TRANSACTION = "tblTransaction"
        const val TABLE_CATINOUT = "tblCatInOut"

        //Inout Table Columns
        const val INOUT_ID = "id"
        const val INOUT_NAME = "name"
        const val INOUT_TYPE = "type"

        //Category Table Columns
        const val CATEGORY_ID = "id"
        const val CATEGORY_NAME = "name"
        const val CATEGORY_ICON = "icon"
        const val CATEGORY_NOTE = "note"
        const val CATEGORY_PARENT_ID = "parentId"

        //CatInOut Table Columns
        const val CATINOUT_ID = "id"
        const val CATINOUT_CAT_ID = "catId"
        const val CATINOUT_INOUT_ID = "inoutId"

        //Transaction Table Columns
        const val TRANSACTION_ID = "id"
        val TRANSACTION_NAME = "name"
        const val TRANSACTION_AMOUNT = "amount"
        const val TRANSACTION_DATE = "date"
        const val TRANSACTION_NOTE = "note"
        const val TRANSACTION_CATINOUT_ID = "catInOutId"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCategoryTable =
            ("CREATE TABLE $TABLE_CATEGORY ($CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $CATEGORY_NAME TEXT, $CATEGORY_ICON TEXT, $CATEGORY_NOTE TEXT, $CATEGORY_PARENT_ID INTEGER)")
        db.execSQL(createCategoryTable)

        val createInOutTable =
            ("CREATE TABLE $TABLE_INOUT ($INOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT, $INOUT_NAME TEXT, $INOUT_TYPE INTEGER)")
        db.execSQL(createInOutTable)

        val createCatInOutTable =
            ("CREATE TABLE $TABLE_CATINOUT ($CATINOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$CATINOUT_CAT_ID INTEGER, $CATINOUT_INOUT_ID INTEGER, " +
                    "FOREIGN KEY($CATINOUT_CAT_ID) REFERENCES $TABLE_CATEGORY($CATEGORY_ID), " +
                    "FOREIGN KEY($CATINOUT_INOUT_ID) REFERENCES $TABLE_INOUT($INOUT_ID))")
        db.execSQL(createCatInOutTable)

        val createTransactionTable =
            ("CREATE TABLE $TABLE_TRANSACTION ($TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT, $TRANSACTION_NAME TEXT, $TRANSACTION_AMOUNT REAL, $TRANSACTION_DATE TEXT, $TRANSACTION_NOTE TEXT, $TRANSACTION_CATINOUT_ID INTEGER)")

        db.execSQL(createTransactionTable)

        fakeData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    fun deleteDatabase(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
    }

    @SuppressLint("Range")
    fun getAllTransaction(): List<Transaction> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TRANSACTION", null)
        val list = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(TRANSACTION_ID))
            val name = cursor.getString(cursor.getColumnIndex(TRANSACTION_NAME))
            val amount = cursor.getDouble(cursor.getColumnIndex(TRANSACTION_AMOUNT))
            val date = cursor.getString(cursor.getColumnIndex(TRANSACTION_DATE))
            val note = cursor.getString(cursor.getColumnIndex(TRANSACTION_NOTE))
            val catInOutId = cursor.getInt(cursor.getColumnIndex(TRANSACTION_CATINOUT_ID))
            list.add(Transaction(id, name, getCatInOutById(catInOutId), amount, date, note))
        }
        cursor.close()
        db.close()
        return list
    }

    @SuppressLint("Range")
    fun getAllTransactionByDay(): List<Transaction> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TRANSACTION WHERE $TRANSACTION_DATE = date('now')", null)
        val list = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(TRANSACTION_ID))
            val name = cursor.getString(cursor.getColumnIndex(TRANSACTION_NAME))
            val amount = cursor.getDouble(cursor.getColumnIndex(TRANSACTION_AMOUNT))
            val date = cursor.getString(cursor.getColumnIndex(TRANSACTION_DATE))
            val note = cursor.getString(cursor.getColumnIndex(TRANSACTION_NOTE))
            val catInOutId = cursor.getInt(cursor.getColumnIndex(TRANSACTION_CATINOUT_ID))
            list.add(Transaction(id, name, getCatInOutById(catInOutId), amount, date, note))
        }
        cursor.close()
        db.close()
        return list
    }

    @SuppressLint("Range")
    fun getCatInOutById(catInOutId: Int): CatInOut {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CATINOUT WHERE $CATINOUT_ID = $catInOutId",
            null,
        )
        cursor.moveToFirst()
        val catInOut = CatInOut(
            cursor.getInt(cursor.getColumnIndex(CATINOUT_ID)),
            getCategoryById(cursor.getInt(cursor.getColumnIndex(CATINOUT_CAT_ID))),
            getInOutById(cursor.getInt(cursor.getColumnIndex(CATINOUT_INOUT_ID)))
        )
        cursor.close()
        db.close()
        return catInOut
    }

    @SuppressLint("Range")
    fun getCatInoutByCatInout(catId: Int, inoutId: Int): CatInOut {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CATINOUT WHERE $CATINOUT_CAT_ID = $catId AND $CATINOUT_INOUT_ID = $inoutId",
            null
        )
        cursor.moveToFirst()
        val catInOut = CatInOut(
            cursor.getInt(cursor.getColumnIndex(CATINOUT_ID)),
            getCategoryById(cursor.getInt(cursor.getColumnIndex(CATINOUT_CAT_ID))),
            getInOutById(cursor.getInt(cursor.getColumnIndex(CATINOUT_INOUT_ID)))
        )
        cursor.close()
        db.close()
        return catInOut
    }

    @SuppressLint("Range")
    fun getCategoryById(id: Int): Category {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CATEGORY WHERE $CATEGORY_ID = $id",
            null

        )
        cursor.moveToFirst()
        val category = Category(
            cursor.getInt(cursor.getColumnIndex(CATEGORY_ID)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_ICON)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_NOTE)),
            null
        )
        cursor.close()
        db.close()
        return category
    }

    @SuppressLint("Range")
    fun getInOutById(id: Int): InOut {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_INOUT,
            arrayOf(INOUT_ID, INOUT_NAME, INOUT_TYPE),
            "$INOUT_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val inOut = InOut(
            cursor.getInt(cursor.getColumnIndex(INOUT_ID)),
            cursor.getString(cursor.getColumnIndex(INOUT_NAME)),
            cursor.getInt(cursor.getColumnIndex(INOUT_TYPE))
        )
        cursor.close()
        db.close()
        return inOut
    }

    fun insertCategory(category: Category) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(CATEGORY_NAME, category.name)
        values.put(CATEGORY_ICON, category.icon)
        values.put(CATEGORY_NOTE, category.note)
        values.put(CATEGORY_PARENT_ID, category.parent?.id ?: 0)
        db.insert(TABLE_CATEGORY, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getCategory(id: Int): Category {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CATEGORY,
            arrayOf(CATEGORY_ID, CATEGORY_NAME, CATEGORY_ICON, CATEGORY_NOTE, CATEGORY_PARENT_ID),
            "$CATEGORY_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        val category = Category(
            cursor.getInt(cursor.getColumnIndex(CATEGORY_ID)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_ICON)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_NOTE)),
            null
        )
        cursor.close()
        return category
    }

    fun addTransaction(transaction: Transaction): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TRANSACTION_NAME, transaction.name)
        values.put(TRANSACTION_AMOUNT, transaction.amount)
        values.put(TRANSACTION_DATE, transaction.date)
        values.put(TRANSACTION_NOTE, transaction.note)
        values.put(TRANSACTION_CATINOUT_ID, transaction.catInOut.id)
        val success = db.insert(TABLE_TRANSACTION, null, values)
        db.close()
        return success > 0
    }

    fun getTransaction(id: Int) {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TRANSACTION,
            arrayOf(
                TRANSACTION_ID,
                TRANSACTION_NAME,
                TRANSACTION_AMOUNT,
                TRANSACTION_DATE,
                TRANSACTION_NOTE,
                TRANSACTION_CATINOUT_ID
            ),
            "$TRANSACTION_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        cursor.close()
    }

    @SuppressLint("Range")
    fun getCategoryByParentId(parentId: Int): List<Category> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CATEGORY WHERE $CATEGORY_PARENT_ID = $parentId",
            null
        )
        val list = mutableListOf<Category>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))
            val name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME))
            val icon = cursor.getString(cursor.getColumnIndex(CATEGORY_ICON))
            val note = cursor.getString(cursor.getColumnIndex(CATEGORY_NOTE))
            val parent = cursor.getInt(cursor.getColumnIndex(CATEGORY_PARENT_ID))

            Log.e("error", id.toString() +" " + name + " " + icon + " " + note + " " + parent)
            list.add(
                Category(
                    id,
                    name,
                    icon,
                    note,
                    if (parent == 0) null else getCategoryById(parent)
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    @SuppressLint("Range")
    fun getCategoryByInOut(type: Int): List<Category> {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT DISTINCT $TABLE_CATEGORY.* FROM $TABLE_CATEGORY JOIN $TABLE_CATINOUT ON $TABLE_CATEGORY.$CATEGORY_ID = $TABLE_CATINOUT.$CATINOUT_CAT_ID JOIN $TABLE_INOUT ON $TABLE_CATINOUT.$CATINOUT_INOUT_ID = $TABLE_INOUT.$INOUT_ID WHERE $TABLE_INOUT.$INOUT_TYPE = $type",
            null
        )
        val list = mutableListOf<Category>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))
            val name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME))
            val icon = cursor.getString(cursor.getColumnIndex(CATEGORY_ICON))
            val note = cursor.getString(cursor.getColumnIndex(CATEGORY_NOTE))
            val parent = cursor.getInt(cursor.getColumnIndex(CATEGORY_PARENT_ID))
            list.add(
                Category(
                    id,
                    name,
                    icon,
                    note,
                    if (parent == 0) null else getCategoryById(parent)
                )
            )
        }

        for (category in list) {
            Log.d("Category", category.toString())
        }
        cursor.close()
        db.close()
        return list
    }

    @SuppressLint("Range")
    fun getCategoryByName(name: String): Category {
        val db = this.readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_CATEGORY WHERE $CATEGORY_NAME = '$name'",
            null
        )

        cursor.moveToFirst()
        return Category(
            cursor.getInt(cursor.getColumnIndex(CATEGORY_ID)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_NAME)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_ICON)),
            cursor.getString(cursor.getColumnIndex(CATEGORY_NOTE)),
            if (cursor.getInt(cursor.getColumnIndex(CATEGORY_PARENT_ID)) == 0) null else
                getCategoryById(cursor.getInt(cursor.getColumnIndex(CATEGORY_PARENT_ID)))
        )
    }

    @SuppressLint("Range")
    fun getInOutByType(type: Int): InOut {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_INOUT WHERE $INOUT_TYPE = $type",
            null
        )
        cursor.moveToFirst()
        return InOut(
            cursor.getInt(cursor.getColumnIndex(INOUT_ID)),
            cursor.getString(cursor.getColumnIndex(INOUT_NAME)),
            cursor.getInt(cursor.getColumnIndex(INOUT_TYPE))
        )
    }


    private fun fakeData(db: SQLiteDatabase) {
        //fake data for category
        val values = ContentValues()
        values.put(CATEGORY_NAME, "Salary")
        values.put(CATEGORY_ICON, "ic_salary")
        values.put(CATEGORY_NOTE, "Salary note")
        values.put(CATEGORY_PARENT_ID, 0)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Scholarship")
        values.put(CATEGORY_ICON, "ic_scholarship")
        values.put(CATEGORY_NOTE, "Scholarship note")
        values.put(CATEGORY_PARENT_ID, 0)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Work")
        values.put(CATEGORY_ICON, "ic_work")
        values.put(CATEGORY_NOTE, "Work note")
        values.put(CATEGORY_PARENT_ID, 0)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Gift")
        values.put(CATEGORY_ICON, "ic_gift")
        values.put(CATEGORY_NOTE, "Gift note")
        values.put(CATEGORY_PARENT_ID, 0)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Study")
        values.put(CATEGORY_ICON, "ic_study")
        values.put(CATEGORY_NOTE, "Study note")
        values.put(CATEGORY_PARENT_ID, 0)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Study fee")
        values.put(CATEGORY_ICON, "ic_study_fee")
        values.put(CATEGORY_NOTE, "Study fee note")
        values.put(CATEGORY_PARENT_ID, 5)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Book")
        values.put(CATEGORY_ICON, "ic_book")
        values.put(CATEGORY_NOTE, "Book note")
        values.put(CATEGORY_PARENT_ID, 5)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Life")
        values.put(CATEGORY_ICON, "ic_life")
        values.put(CATEGORY_NOTE, "Life note")
        values.put(CATEGORY_PARENT_ID, 0)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "Market")
        values.put(CATEGORY_ICON, "ic_market")
        values.put(CATEGORY_NOTE, "Market note")
        values.put(CATEGORY_PARENT_ID, 8)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        values.put(CATEGORY_NAME, "House")
        values.put(CATEGORY_ICON, "ic_house")
        values.put(CATEGORY_NOTE, "House note")
        values.put(CATEGORY_PARENT_ID, 8)
        db.insert(TABLE_CATEGORY, null, values)
        values.clear()

        //fake data for transaction
        values.put(TRANSACTION_NAME, "Food")
        values.put(TRANSACTION_AMOUNT, 10000)
        values.put(TRANSACTION_DATE, "2024-10-08")
        values.put(TRANSACTION_NOTE, "Food note")
        values.put(TRANSACTION_CATINOUT_ID, 1)
        db.insert(TABLE_TRANSACTION, null, values)

        values.clear()

        values.put(TRANSACTION_NAME, "Drink")
        values.put(TRANSACTION_AMOUNT, 5000)
        values.put(TRANSACTION_DATE, "2024-10-08")
        values.put(TRANSACTION_NOTE, "Drink note")
        values.put(TRANSACTION_CATINOUT_ID, 2)
        db.insert(TABLE_TRANSACTION, null, values)

        values.clear()

        //fake data for catinout
        values.put(CATINOUT_CAT_ID, 1)
        values.put(CATINOUT_INOUT_ID, 1)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 2)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 3)
        values.put(CATINOUT_INOUT_ID, 1)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 4)
        values.put(CATINOUT_INOUT_ID, 1)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 5)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 6)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 7)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 8)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 9)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        values.put(CATINOUT_CAT_ID, 10)
        values.put(CATINOUT_INOUT_ID, 2)
        db.insert(TABLE_CATINOUT, null, values)
        values.clear()

        //fake data for inout
        values.put(INOUT_NAME, "Income")
        values.put(INOUT_TYPE, 0)
        db.insert(TABLE_INOUT, null, values)
        values.clear()

        values.put(INOUT_NAME, "Outcome")
        values.put(INOUT_TYPE, 1)
        db.insert(TABLE_INOUT, null, values)
        values.clear()

        values.put(INOUT_NAME, "Income-Outcome")
        values.put(INOUT_TYPE, 2)
        db.insert(TABLE_INOUT, null, values)

        values.clear()
    }
}