package info.tangchen.slidebutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SlideButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.sd);

        button.bindListener(new Slide2TheEndListener() {
            @Override
            public void changeState() {
                Toast.makeText(MainActivity.this, "滑动到底部！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
