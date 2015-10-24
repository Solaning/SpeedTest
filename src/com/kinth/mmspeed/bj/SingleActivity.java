package com.kinth.mmspeed.bj;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kinth.mmspeed.BaseActivity;
import com.kinth.mmspeed.R;
import com.kinth.mmspeed.RMGInviteMemberActivity;
import com.kinth.mmspeed.bj.SortCursor.SortEntry;

@SuppressLint("NewApi")
public class SingleActivity extends BaseActivity {
	// 联系人列表的界面
	private View contactsView;
	// 字母列视图View
	private AlphabetScrollBar m_asb;
	// 显示选中的字母
	private TextView m_letterNotice;
	// 联系人的列表
	private ListView m_contactslist;
	// 联系人列表的适配器
	private ContactsCursorAdapter m_contactsAdapter;
//	// 筛选后的适配器
//	private FilterAdapter m_FAdapter;
	// 筛选查询后的数据list
	private ArrayList<SortEntry> mFilterList = new ArrayList<SortEntry>();
	// 加载器监听器
	private Cursor cursor;
	private ContactsLoaderListener m_ContactsCallback = new ContactsLoaderListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single);
		 initView();
	}
	
	private void initView() {
		
		//得到字母列的对象,并设置触摸响应监听器
		m_asb = (AlphabetScrollBar)findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener(new ScrollBarListener());
		m_letterNotice = (TextView)findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);
		
		//得到联系人列表,并设置适配器
		SingleActivity.this.getLoaderManager().initLoader(0,null,m_ContactsCallback);
		
		m_contactslist = (ListView)findViewById(R.id.pb_listvew);
		m_contactsAdapter = new ContactsCursorAdapter(SingleActivity.this, null);
		m_contactslist.setAdapter(m_contactsAdapter);
		
	}
	// 加载器的监听器
	private class ContactsLoaderListener implements
			LoaderManager.LoaderCallbacks<Cursor> {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new SortCursorLoader(SingleActivity.this,
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);
		}
		
		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			cursor = arg1;
			m_contactsAdapter.swapCursor(arg1);
			m_contactslist.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					//Toast.makeText(SingleActivity.this, cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+"", Toast.LENGTH_SHORT).show();
					String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+"";
					if (!phone.equals("")) {
						Intent intent = new Intent(SingleActivity.this,RMGInviteMemberActivity.class);
						intent.putExtra(RMGInviteMemberActivity.INTENT_RESULT_PHONE
								, phone.replace("-", "").trim());
						setResult(RESULT_OK, intent);
						finish();
						rightOutFinishAnimation();
					}
				}
			});
//			SortCursor data = (SortCursor) m_contactsAdapter.getCursor();
//			mFilterList = data.FilterSearch(m_FilterEditText.getText()
//						.toString().trim());
//				// m_FAdapter.notifyDataSetChanged();
//			m_FAdapter = new FilterAdapter(getActivity(), mFilterList);
//			m_contactslist.setAdapter(m_FAdapter);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			m_contactsAdapter.swapCursor(null);

		}

	}
	
	
	/*
	private class ContactsCursorAdapter extends CursorAdapter {
		int ItemPos = -1;
		private List<SortCursor.SortEntry> mList;
		private Context context;

		public ContactsCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor);
			this.context = context;
			// TODO Auto-generated constructor stub
			if (cursor!=null) {
				SortCursor data = (SortCursor)cursor;
				mList = data.GetContactsArray();
			}else {
				mList = new ArrayList<SortCursor.SortEntry>();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (null == convertView) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.contacts_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.ivChoose = (ImageView) convertView
						.findViewById(R.id.choose_contact);
				
				viewHolder.tvName = (TextView) convertView
						.findViewById(R.id.contacts_name);
				
				viewHolder.tvPhone = (TextView) convertView
						.findViewById(R.id.contacts_number);
				viewHolder.tvLetter = (TextView) convertView
						.findViewById(R.id.pb_item_LetterTag);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
//			viewHolder.ivChoose.setText(mList.get(position).mchoose);
//			viewHolder.tvName.setText(mList.get(position).mName);
//			viewHolder.tvPhone.setText(mList.get(position).mNum());
//			viewHolder.tvLetter.setText(mList.get(position).getShortContent());
			
			return convertView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if (cursor == null) {
				return;
			}
			
			TextView name = (TextView) view.findViewById(R.id.contacts_name);
			name.setText(mList.get(arg0));

			TextView number = (TextView) view.findViewById(R.id.contacts_number);
			number.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

			ImageView chooseView = (ImageView) view.findViewById(R.id.choose_contact);
			chooseView.setVisibility(View.GONE);

			// 字母提示textview的显示
			TextView letterTag = (TextView) view.findViewById(R.id.pb_item_LetterTag);
			// 获得当前姓名的拼音首字母
			String firstLetter = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
					.substring(0, 1).toUpperCase();

			// 如果是第1个联系人 那么letterTag始终要显示
			if (ItemPos == 0) {
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			} else {
				// 获得上一个姓名的拼音首字母
				cursor.moveToPrevious();
				String firstLetterPre = PinyinUtils
						.getPingYin(
								cursor.getString(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)))
						.substring(0, 1).toUpperCase();
				// 比较一下两者是否相同
				if (firstLetter.equals(firstLetterPre)) {
					letterTag.setVisibility(View.GONE);
				} else {
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
			}

		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(
					R.layout.contacts_list_item, parent, false);
		}
	}
	 */
	// 字母列触摸的监听器
	private class ScrollBarListener implements AlphabetScrollBar.OnTouchBarListener {
		@Override
		public void onTouch(String letter) {
			// 触摸字母列时,将联系人列表更新到首字母出现的位置
			SortCursor ContactsCursor = (SortCursor) m_contactsAdapter
					.getCursor();
			if (ContactsCursor != null) {
				int idx = ContactsCursor.binarySearch(letter);
				if (idx != -1) {
					m_contactslist.setSelection(idx);
				}
			}
		}
	}
	private class ContactsCursorAdapter extends CursorAdapter{
		int ItemPos = -1;
		private ArrayList<SortCursor.SortEntry> list;
		private Context context;
		
		public ContactsCursorAdapter(Context context, Cursor c) {
			super(context, c);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemPos = position;

			return super.getView(position, convertView, parent);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if(cursor == null)
			{
				return;
			}
            TextView name = (TextView) view.findViewById(R.id.contacts_name);
            name.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
    	    
            TextView number = (TextView) view.findViewById(R.id.contacts_number);
            number.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            
            ImageView chooseView = (ImageView)view.findViewById(R.id.choose_contact);
            chooseView.setVisibility(View.GONE);
            
			//字母提示textview的显示 
			TextView letterTag = (TextView)view.findViewById(R.id.pb_item_LetterTag);
			//获得当前姓名的拼音首字母
			String firstLetter = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).substring(0,1).toUpperCase();
			
			//如果是第1个联系人 那么letterTag始终要显示
			if(ItemPos == 0)
			{
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			}			
			else
			{
				//获得上一个姓名的拼音首字母
				cursor.moveToPrevious();
				String firstLetterPre = PinyinUtils.getPingYin(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).substring(0,1).toUpperCase();
				//比较一下两者是否相同
				if(firstLetter.equals(firstLetterPre))
				{
					letterTag.setVisibility(View.GONE);
				}
				else 
				{
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
			}
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.contacts_list_item, parent, false);
		}
		
	}
	private final class ViewHolder {
		ImageView ivChoose;
		TextView tvName;
		TextView tvPhone;
		TextView tvLetter;
	}
	@Override
	public void leftBtnOnClick(View v) {
		this.finish();
		Intent intent = new Intent(SingleActivity.this,RMGInviteMemberActivity.class);
		intent.putExtra(RMGInviteMemberActivity.INTENT_RESULT_PHONE
				, "");
		setResult(RESULT_OK, intent);
		finish();
		rightOutFinishAnimation();
	}
	
	public void leftBtnOnclick(View v){
		finish();
		rightOutFinishAnimation();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			Intent intent = new Intent(SingleActivity.this,RMGInviteMemberActivity.class);
			intent.putExtra(RMGInviteMemberActivity.INTENT_RESULT_PHONE
					, "");
			setResult(RESULT_OK, intent);
			finish();
			rightOutFinishAnimation();
			rightOutFinishAnimation();
			return false;
		}
		return false;
	}
}
