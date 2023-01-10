package com.my_application.gymproject;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

//импорт данных БД и значений с фронта
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.my_application.gymproject.models.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegistration;
    FirebaseAuth auth; //для автризации
    FirebaseDatabase db; //для подключения к бд
    DatabaseReference users; //для работы с таблицами в бд
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        root = findViewById(R.id.root_element);
        //привязка кнопки "Войти" к переменной btnSignIn
        btnSignIn = findViewById(R.id.btnSignIn);
        //привязка кнопки "Регистрация" к переменной btnRegistration
        btnRegistration = findViewById(R.id.btnRegistration);

        //Запуск авторизации в БД
        auth = FirebaseAuth.getInstance();
        //Подключение к БД
        db = FirebaseDatabase.getInstance();
        //Указываем с какой таблицей работать
        users = db.getReference("Users");

        //Обработчик событий для кнопки Регистрации
        btnRegistration.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showRegisterWindow();
           }
       });

    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Зарегистрироваться!");
        dialog.setMessage("Введите все данные для регистрации");

        //получаем уже созданные шаблон регистрации

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_window = inflater.inflate(R.layout.register_window, null);
        dialog.setView(register_window);

        final MaterialEditText email = register_window.findViewById(R.id.emailField);
        final MaterialEditText password = register_window.findViewById(R.id.passField);
        final MaterialEditText name = register_window.findViewById(R.id.nameField);
        final MaterialEditText phone = register_window.findViewById(R.id.phoneField);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())){
                    Snackbar.make(root, "Введите вашу почту", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(name.getText().toString())){
                    Snackbar.make(root, "Введите ваше имя", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(phone.getText().toString())){
                    Snackbar.make(root, "Введите ваш номер телефона", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().length() < 6){
                    Snackbar.make(root, "Введите пароль более 6 символов", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                //регистраци
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(email.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPass(password.getText().toString());
                                user.setPhone(phone.getText().toString());

                                users.child(user.getEmail())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Snackbar.make(root, "Пользователь добавлен!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
        });
        dialog.show();
    }
}