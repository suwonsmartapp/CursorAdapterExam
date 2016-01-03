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
