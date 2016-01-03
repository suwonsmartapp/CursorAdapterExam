# CursorAdapter & ListView Listener
DatabaseExam 에 CursorAdapter 와 ListView 관련 리스너 2개에 대한 예제입니다.

# 1. CursorAdapter
```java
package com.massivcode.cursoradapterexam;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.massivcode.cursoradapterexam.database.ExamDbContract;

/**
 * 지금까지 리스트뷰를 이용하면서 BaseAdapter 를 어댑터로 사용했습니다.
 * CursorAdapter 는 쿼리의 결과인 Cursor 객체의 데이터를 이용하여 데이터를 출력하는데 사용됩니다.
 */
public class ExamCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    /**
     * 커서 어댑터의 여러가지 생성자 중 가장 많이 사용되는 생성자 입니다.
     * @param context : 컨텍스트
     * @param c : 데이터로 사용할 커서 객체
     * @param autoRequery : false
     */
    public ExamCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        View view = mInflater.inflate(R.layout.item_listview, parent, false);
        viewHolder.dataTextView = (TextView) view.findViewById(R.id.item_tv);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String data = cursor.getString(cursor.getColumnIndexOrThrow(ExamDbContract.ExamDbEntry.COLUMN_NAME_DATA));
        viewHolder.dataTextView.setText(data);
    }

    static class ViewHolder {
        TextView dataTextView;
    }
}

```

# 2. Listener
```java
package com.massivcode.cursoradapterexam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.massivcode.cursoradapterexam.database.ExamDbFacade;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private EditText mInputEditText;
    private Button mInsertButton, mSelectButton, mUpdateButton, mDeleteButton;
    private ExamDbFacade mFacade;
    private ListView mListView;

    private ExamCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mInputEditText = (EditText) findViewById(R.id.input_et);
        mInsertButton = (Button) findViewById(R.id.insert_btn);
        mSelectButton = (Button) findViewById(R.id.select_btn);
        mUpdateButton = (Button) findViewById(R.id.update_btn);
        mDeleteButton = (Button) findViewById(R.id.delete_btn);

        mInsertButton.setOnClickListener(this);
        mSelectButton.setOnClickListener(this);
        mUpdateButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);

        mFacade = new ExamDbFacade(getApplicationContext());
        mAdapter = new ExamCursorAdapter(getApplicationContext(), mFacade.getCursor(), false);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        // 리스트 뷰의 각각의 아이템을 클릭했을 때의 리스너 입니다.
        mListView.setOnItemClickListener(this);
        // 리스트 뷰의 각각의 아이템을 롱클릭했을 때의 리스너 입니다.
        mListView.setOnItemLongClickListener(this);
    }

    private boolean checkString(String str) {
        boolean result = false;

        if (TextUtils.isEmpty(str)) {
            result = true;
        }

        return result;
    }

    @Override
    public void onClick(View v) {

        String str = mInputEditText.getText().toString();
        boolean isNull = checkString(str);

        if (v.getId() != R.id.select_btn) {
            if (isNull == true) {
                Toast.makeText(MainActivity.this, "입력해주세요!", Toast.LENGTH_SHORT).show();
            }
        }

        switch (v.getId()) {
            // DB에 데이터를 삽입하는 버튼입니다.
            case R.id.insert_btn:
                if (isNull == false) {
                    ArrayList<String> insertResult = mFacade.insert(str);
                    mAdapter.changeCursor(mFacade.getCursor());
                }
                break;
            // DB에서 모든 데이터를 조회하여 출력하는 버튼입니다.
            case R.id.select_btn:
                ArrayList<String> selectResult = mFacade.select();
                mAdapter.changeCursor(mFacade.getCursor());
                break;
            // DB의 모든 데이터를 입력한 값으로 변경하는 버튼입니다.
            case R.id.update_btn:
                if (isNull == false) {
                    ArrayList<String> updateResult = mFacade.update(str);
                    mAdapter.changeCursor(mFacade.getCursor());
                }
                break;
            // 입력한 값을 가지는 데이터를 삭제하는 버튼입니다.
            case R.id.delete_btn:
                if (isNull == false) {
                    ArrayList<String> deleteResult = mFacade.delete(str);
                    mAdapter.changeCursor(mFacade.getCursor());
                }
                break;
        }

    }

    /**
     * 리스트 뷰의 각각의 아이템을 클릭했을 때의 리스너 입니다.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(MainActivity.this, "클릭한 position : " + position, Toast.LENGTH_SHORT).show();
        System.out.println("클릭한 item이 보유한 id : " + parent.getItemIdAtPosition(position));
    }

    /**
     * 리스트 뷰의 각각의 아이템을 롱클릭했을 때의 리스너 입니다.
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(MainActivity.this, "클릭한 position : " + position, Toast.LENGTH_SHORT).show();
        System.out.println("클릭한 item이 보유한 id : " + parent.getItemIdAtPosition(position));
        return true;
    }
}

```
