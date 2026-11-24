package ru.partner.workoutjournal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    DB db;
    LinearLayout root, history;
    EditText date, sets, reps, weight;
    Spinner exercise;
    final String[] exercises = {"Жим штанги лёжа","Приседания со штангой","Жим гантелей","Подъём на бицепс","Становая тяга","Тяга штанги в наклоне","Жим ногами","Подтягивания","Отжимания на брусьях","Другое"};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        db = new DB();
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this); root.setOrientation(1); root.setPadding(28,24,28,24);
        scroll.addView(root); setContentView(scroll);
        TextView title = new TextView(this); title.setText("ДНЕВНИК ТРЕНИРОВОК"); title.setTextSize(23); title.setTypeface(null, Typeface.BOLD);
        root.addView(title);
        TextView sub = new TextView(this); sub.setText("Записывай упражнения, подходы, повторения и рабочий вес."); sub.setTextSize(14); sub.setPadding(0,8,0,20); root.addView(sub);

        label("Дата тренировки");
        date = new EditText(this); date.setText(today()); date.setFocusable(false); date.setOnClickListener(v -> pickDate()); root.addView(date, lp());
        label("Упражнение");
        exercise = new Spinner(this); exercise.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, exercises)); root.addView(exercise, lp());
        label("Количество подходов"); sets = field("Например, 4", true);
        label("Повторения в подходе"); reps = field("Например, 10", true);
        label("Вес, кг"); weight = field("Например, 60", false);

        Button save = new Button(this); save.setText("СОХРАНИТЬ УПРАЖНЕНИЕ"); root.addView(save, lp());
        save.setOnClickListener(v -> save());
        TextView h = new TextView(this); h.setText("История тренировок"); h.setTextSize(20); h.setTypeface(null, Typeface.BOLD); h.setPadding(0,28,0,12); root.addView(h);
        history = new LinearLayout(this); history.setOrientation(1); root.addView(history);
        refresh();
    }
    LinearLayout.LayoutParams lp(){ return new LinearLayout.LayoutParams(-1,-2); }
    void label(String s){ TextView t=new TextView(this); t.setText(s); t.setTextSize(15); t.setTypeface(null,Typeface.BOLD); t.setPadding(0,12,0,4); root.addView(t); }
    EditText field(String hint, boolean integer){ EditText e=new EditText(this); e.setSingleLine(true); e.setHint(hint); e.setInputType(integer?android.text.InputType.TYPE_CLASS_NUMBER:android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); root.addView(e,lp()); return e; }
    String today(){ return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date()); }
    void pickDate(){
        Calendar c=Calendar.getInstance();
        new DatePickerDialog(this,(v,y,m,d)->date.setText(String.format(Locale.getDefault(),"%02d.%02d.%04d",d,m+1,y)),c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }
    void save(){
        String s=sets.getText().toString().trim(), r=reps.getText().toString().trim(), w=weight.getText().toString().trim();
        if(s.isEmpty()||r.isEmpty()||w.isEmpty()){ Toast.makeText(this,"Заполни подходы, повторения и вес",Toast.LENGTH_SHORT).show(); return; }
        try {
            int n=Integer.parseInt(s), repsN=Integer.parseInt(r); double kg=Double.parseDouble(w.replace(',','.'));
            if(n<1||repsN<1||kg<0) throw new Exception();
            ContentValues cv=new ContentValues(); cv.put("date",date.getText().toString()); cv.put("exercise",exercises[exercise.getSelectedItemPosition()]);
            cv.put("sets",n); cv.put("reps",repsN); cv.put("weight",kg); db.getWritableDatabase().insert("workouts",null,cv);
            sets.setText(""); reps.setText(""); weight.setText(""); refresh();
            Toast.makeText(this,"Упражнение сохранено",Toast.LENGTH_SHORT).show();
        } catch(Exception ex){ Toast.makeText(this,"Проверь введённые значения",Toast.LENGTH_SHORT).show(); }
    }
    void refresh(){
        history.removeAllViews();
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,date,exercise,sets,reps,weight FROM workouts ORDER BY id DESC",null);
        if(c.getCount()==0){ TextView empty=new TextView(this); empty.setText("Пока нет записей. Добавь первое упражнение выше."); empty.setPadding(0,8,0,20); history.addView(empty); }
        while(c.moveToNext()){
            final int id=c.getInt(0);
            String text=c.getString(1)+"\n"+c.getString(2)+"\n"+c.getInt(3)+" подхода × "+c.getInt(4)+" повторений · "+c.getString(5)+" кг";
            LinearLayout row=new LinearLayout(this); row.setOrientation(1); row.setPadding(14,12,14,12);
            TextView tv=new TextView(this); tv.setText(text); tv.setTextSize(16); row.addView(tv);
            Button del=new Button(this); del.setText("Удалить запись"); del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Удалить запись?").setMessage("Запись будет удалена из истории.").setNegativeButton("Отмена",null).setPositiveButton("Удалить",(a,b)->{db.getWritableDatabase().delete("workouts","id=?",new String[]{String.valueOf(id)});refresh();}).show());
            row.addView(del); history.addView(row);
            View line=new View(this); line.setBackgroundColor(0xFFE0E0E0); history.addView(line,new LinearLayout.LayoutParams(-1,1));
        }
        c.close();
    }
    class DB extends SQLiteOpenHelper {
        DB(){super(MainActivity.this,"workout_journal.db",null,1);}
        public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE workouts(id INTEGER PRIMARY KEY AUTOINCREMENT,date TEXT,exercise TEXT,sets INTEGER,reps INTEGER,weight REAL)");}
        public void onUpgrade(SQLiteDatabase d,int oldV,int newV){}
    }
}
