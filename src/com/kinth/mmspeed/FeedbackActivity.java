package com.kinth.mmspeed;

import java.util.HashMap;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.network.CommonMsgInfo;
import com.kinth.mmspeed.util.ApplicationController;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 意见反馈界面
 * 
 * @author BJ
 */
public class FeedbackActivity extends BaseActivity {
	
	@ViewInject(R.id.user_feedback_contact_et)
	private EditText edtContrat;
	
	@ViewInject(R.id.user_feedback_content_et)
	private EditText edtContent;
	
	@ViewInject(R.id.nav_right)
	private ImageView btnNavRight;
	
	@ViewInject(R.id.nav_tittle)
	private TextView tvTittle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feedback);
		ViewUtils.inject(this);
		tvTittle.setText("意见反馈");
	}

	@OnClick(R.id.nav_right)
	public void submitOnClick(View v) {
		onSubmitBtnHandler();
	}

	// 提交反馈
	private void onSubmitBtnHandler() {
		String contact = edtContrat.getText().toString();
		String content = edtContent.getText().toString();
		if (content.trim().equals("")) {
			Toast.makeText(this, "亲，请填写您的反馈内容", Toast.LENGTH_SHORT).show();
		} else {
			HashMap<String, String> params = CommonMsgInfo.getCommonMsgInfo();
			params.put("email", UtilFunc.encode(contact));
			params.put("content", UtilFunc.encode(content));
			JsonObjectRequest req = new JsonObjectRequest(
					APPConstant.USER_FEEDBACK_URL, new JSONObject(params),
					new Response.Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							int rtn = 0;
							try {
								rtn = response.getInt("rtn");
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (rtn == 1) {
								Toast.makeText(FeedbackActivity.this,
										"感谢您的来信，我们会尽快处理", Toast.LENGTH_SHORT)
										.show();
								finish();
							} else {
								Toast.makeText(FeedbackActivity.this,
										"网络异常，请稍后重试", Toast.LENGTH_SHORT)
										.show();
								finish();
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.e("Error: ", error.getMessage());
							Toast.makeText(FeedbackActivity.this,
									"网络异常，请稍后重试", Toast.LENGTH_SHORT)
									.show();
							finish();
						}
					});
			ApplicationController.getInstance().addToRequestQueue(req);
		}
	}
}
