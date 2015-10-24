package com.kinth.mmspeed;

import java.util.ArrayList;

import com.kinth.mmspeed.bean.ContractInfo;
import com.kinth.mmspeed.constant.APPConstant;
import com.kinth.mmspeed.constant.APPConstant.ActiveType;
import com.kinth.mmspeed.hk.CustomProgressDialogUtil;
import com.kinth.mmspeed.network.AsyncNetworkManager;
import com.kinth.mmspeed.network.AsyncNetworkManager.ChgRichManMemberCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.CreateRichManGroupCallBack;
import com.kinth.mmspeed.network.AsyncNetworkManager.genActiveTimeLineCallBack;
import com.kinth.mmspeed.ui.CommomDialog;
import com.kinth.mmspeed.util.UtilFunc;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.sola.code.ydlly_assist.bean.KeyValue;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 确认选项，选创建还是加入共享
 * 
 * @author Sola
 */
@ContentView(R.layout.activity_rmg_confrim_type)
public class RMGConfirmTypeActivity extends BaseActivity {
	public static final String INTENT_TYPE = "TYPE";
	public static final String INTENT_PHONE = "PHONE";
	public static final String INTENT_TARGET_CLASS_NAME = APPConstant.INTENT_TARGET_CLASS_NAME; //最终需要放到intent中跳转的activity
	
	public static final int TYPE_DEFAULT = 0;
	public static final int TYPE_CREATE_RICH_MAN_GROUP = 1;// 创建群组，发起共享
	public static final int TYPE_INVITE_MEMBER = 2;// 加入共享
	public static final int TYPE_INAUGURATE_DONATION = 3;//流量转赠

	private Context mContext;
	private int type = TYPE_DEFAULT;
	private String phone;// 上个页面传的号码
	private String packageType = APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue();// 默认的套餐类型
	private String groupToJoin;// 要加入的群号码，手输
	private String targetClassName;
	private boolean isCreatRichMamGroupSucceed = false;
	
	@ViewInject(R.id.iv_back_btn)
	private ImageView back;
	
	@ViewInject(R.id.tv_title)
	private TextView title;//标题

	@ViewInject(R.id.layout_create_rich_man_grouop)
	private LinearLayout layoutCreateRMG;// 发起共享的显隐布局

	@ViewInject(R.id.tv_create_rich_man_grouop_phone)
	private TextView showCreateRMGPhone;// 发起共享的号码显示

	@ViewInject(R.id.layout_invite_RMG_member)
	private LinearLayout layoutInviteRMGMember;// 加入共享的显隐布局

	@ViewInject(R.id.et_invite_RMG_member_phone)
	private EditText etInputRMGMemberPhone;// 输入要加入共享的群号码
	
	@ViewInject(R.id.ll_total_package)
	private LinearLayout layoutSelectPackage;//选择套餐的显隐布局

	@ViewInject(R.id.radiogroup1)
	private RadioGroup radioGroup;// 单选框

	@ViewInject(R.id.radiobutton1)
	private RadioButton radiobutton1;//单选按钮

	@ViewInject(R.id.radiobutton2)
	private RadioButton radiobutton2;//单选按钮

	@ViewInject(R.id.tv_package_introduce)
	private TextView packageIntroduce;// 套餐介绍

	@ViewInject(R.id.btn_confirm_select)
	private Button confirmType;// 确认选择
	
	@OnClick(R.id.iv_back_btn)
	public void fun_1(View v){
		back();
	}

	/**
	 * 退出返回
	 */
	private void back() {
		if(TextUtils.isEmpty(targetClassName)){
			finish();
			rightOutFinishAnimation();
			return;
		}
		// 用 setResut() 准备好要回传的数据后，只要使用finish()的方法就能把打包好的数据发给A且运行onActivityResult()部分的代码
		Class<?> clazz = UtilFunc.loadClass(mContext, targetClassName);
		if(clazz != null && (targetClassName.equals(MyFamilyActivity.class.getName()))){
			Intent intent = new Intent(mContext,clazz);
			startActivity(intent);
			intent.putExtra(MyFamilyActivity.RESULT_CREAT_RMG_SUCCEED, isCreatRichMamGroupSucceed);
			setResult(RESULT_OK, intent);
		}
		finish();
		rightOutFinishAnimation(); 
	}

	/**
	 * 重写返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			back();
			return false;
		}
		return false;
	}
	
	@OnClick(R.id.btn_confirm_select)
	public void fun_2(View v) {// 确认按钮，获取短信验证码
		if(type == TYPE_INVITE_MEMBER){//加入共享，判断是否有输入号码
			groupToJoin = etInputRMGMemberPhone.getText().toString();
			if (TextUtils.isEmpty(groupToJoin)) {//TODO
				Toast.makeText(mContext, "请输入要加入流量共享的群号", Toast.LENGTH_LONG).show();
				return;
			}
		}
		Intent intent = new Intent(mContext,
				RMGGetVerificationCodeActivity.class);
		intent.putExtra(RMGGetVerificationCodeActivity.INTENT_PHONE,
				phone);
		intent.putExtra(
				RMGGetVerificationCodeActivity.INTENT_TARGET_CLASS_NAME,
				RMGConfirmTypeActivity.class.getName());
		startActivityForResult(
				intent,
				RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE);
		rightInAnimation();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		type = getIntent().getIntExtra(INTENT_TYPE, TYPE_DEFAULT);
		phone = getIntent().getStringExtra(INTENT_PHONE);
		targetClassName = getIntent().getStringExtra(INTENT_TARGET_CLASS_NAME);
		if (TextUtils.isEmpty(phone)) {
			Log.e("phone", "没有phone");
			return;
		}
		ViewUtils.inject(this);
		switch (type) {
		case TYPE_DEFAULT:
			Log.e("没有类型", "没有类型type");
			break;
		case TYPE_CREATE_RICH_MAN_GROUP:// 创建群组
			title.setText("发起共享");
			layoutInviteRMGMember.setVisibility(View.GONE);
			showCreateRMGPhone.setText(phone);
			packageIntroduce
					.setText("您是否确认作为发起人开通10元套餐(100M)共享流量套餐，首次开通24小时内生效？");
			setRadioGroupListener();
			break;
		case TYPE_INVITE_MEMBER:// 加入群组
			title.setText("加入共享");
			layoutCreateRMG.setVisibility(View.GONE);
			packageIntroduce
					.setText("您是否确认作为参与者开通10元套餐(100M)共享流量套餐，首次开通24小时内生效？");
			setRadioGroupListener();
			break;
		case TYPE_INAUGURATE_DONATION://流量转赠
			title.setText("开通流量转赠套餐");
			layoutInviteRMGMember.setVisibility(View.GONE);
			showCreateRMGPhone.setText(phone);
			layoutSelectPackage.setVisibility(View.GONE);
			packageIntroduce
				.setText("您是否确认开通5元流量转赠套餐，首次开通24小时内生效？");
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode != RMGGetVerificationCodeActivity.INTENT_COMMON_REQUEST_CODE) {// 请求码不同，直接返回
			return;
		}
		switch (resultCode) { // resultCode为回传的标记，在B中回传的是RESULT_OK
		case RESULT_OK:
			String codeStr = data
					.getStringExtra(RMGGetVerificationCodeActivity.INTENT_CODE);
			if (TextUtils.isEmpty(codeStr)) {
				Log.e("回传的验证码为空", "回传的验证码为空");
				return;
			}
			switch(type){
			case TYPE_DEFAULT:
				Log.e("没有类型", "没有类型type");
				break;
			case TYPE_CREATE_RICH_MAN_GROUP:// 创建共享群组
				CustomProgressDialogUtil.startProgressDialog(mContext,
						"正在创建流量共享群组...",true);
				// 去开通流量共享套餐
				new AsyncNetworkManager().createRichManGroup(mContext,APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(), phone,
						packageType, codeStr, new CreateRichManGroupCallBack() {

							@Override
							public void onCreateRichManGroupCallBack(int rtn,String error) {
								CustomProgressDialogUtil.stopProgressDialog();
								CommomDialog.Builder dialog = new CommomDialog.Builder(
										mContext);
								dialog.setTitle("提示");
								dialog.setPositiveButton("确认",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
													int which) {
											}
								});
								switch (rtn) {
								case 1:
									Toast.makeText(mContext, "创建共享群组成功",
											Toast.LENGTH_LONG).show();
									isCreatRichMamGroupSucceed = true;
									dialog = null;
									//产生一条开通流量共享套餐动态
									ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
									keyValue.add(new KeyValue("mobile",phone));
									keyValue.add(new KeyValue("activeType",ActiveType.CreateShareGroup.getValue()));
									new AsyncNetworkManager().genActiveTimeLine(mContext,keyValue,new genActiveTimeLineCallBack() {
										
										@Override
										public void onGenActiveTimeLineCallBack(int rtn,int activeId) {
											//TODO
										}
									});
									return;
								case -1:
									dialog.setMessage("短信验证码失败或过期");
									break;
								case 0:
								default:
									dialog.setMessage(error);
									break;
								}
								dialog.create().show();
							}
						});
				break;
			case TYPE_INVITE_MEMBER:// 加入共享
				CustomProgressDialogUtil.startProgressDialog(mContext,
						"正在加入流量共享群组...",true);
				new AsyncNetworkManager().chgRichManMember(mContext, APPConstant.ServiceID.DO_RICH_MAN_TOGETHER.getValue(),groupToJoin,1,packageType,null,phone,codeStr,new ChgRichManMemberCallBack() {
					
					@Override
					public void onChgRichManMemberCallBack(int rtn,String error) {
						CustomProgressDialogUtil.stopProgressDialog();
						CommomDialog.Builder dialog = new CommomDialog.Builder(
								mContext);
						dialog.setTitle("提示");
						dialog.setPositiveButton("确认",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
						});
						switch (rtn) {
						case 1:
							Toast.makeText(mContext, "加入共享群组成功",
									Toast.LENGTH_LONG).show();
							dialog = null;
							//产生一条加入流量共享套餐动态
							ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
							keyValue.add(new KeyValue("mobile",phone));
							keyValue.add(new KeyValue("activeType",ActiveType.JoinShareGroup.getValue()));
							keyValue.add(new KeyValue("targetMobile", groupToJoin));
							DbUtils db = DbUtils.create(mContext, phone+".friends");
							ContractInfo item = null;
							try {
								item = db.findFirst(Selector.from(ContractInfo.class).where("phoneNumber","=",groupToJoin));
							} catch (DbException e) {
								e.printStackTrace();
							}
							keyValue.add(new KeyValue("targetNickName", UtilFunc.encode(item != null ? item.getContractName() : "")));
							new AsyncNetworkManager().genActiveTimeLine(mContext,keyValue,new genActiveTimeLineCallBack() {
								
								@Override
								public void onGenActiveTimeLineCallBack(int rtn,int activeId) {
									//TODO
								}
							});
							return;
						case -1:
							dialog.setMessage("短信验证码失败或过期");
							break;
						case 0:
						default:
							dialog.setMessage(error);
							break;
						}
						dialog.create().show();
					}
				});
				break;
			case TYPE_INAUGURATE_DONATION://流量转赠 开通
				CustomProgressDialogUtil.startProgressDialog(mContext,
						"正在开通流量转赠套餐...",true);
				new AsyncNetworkManager().createRichManGroup(mContext, APPConstant.ServiceID.I_AM_RICH_MAN.getValue(),phone,
						"", codeStr, new CreateRichManGroupCallBack() {

							@Override
							public void onCreateRichManGroupCallBack(int rtn,String error) {
								CustomProgressDialogUtil.stopProgressDialog();
								CommomDialog.Builder dialog = new CommomDialog.Builder(
										mContext);
								dialog.setTitle("提示");
								dialog.setPositiveButton("确认",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
													int which) {
											}
								});
								switch (rtn) {
								case 1:
									Toast.makeText(mContext, "开通流量转赠套餐成功",
											Toast.LENGTH_LONG).show();
									dialog = null;
									//产生一条朋友圈动态 
									ArrayList<KeyValue> keyValue = new ArrayList<KeyValue>();
									keyValue.add(new KeyValue("mobile",phone));
									keyValue.add(new KeyValue("activeType",ActiveType.OpenDonateFlow.getValue()));
									new AsyncNetworkManager().genActiveTimeLine(mContext,keyValue,new genActiveTimeLineCallBack() {
										
										@Override
										public void onGenActiveTimeLineCallBack(int rtn,int activeId) {
											//TODO
										}
									});
									return;
								case -1:
									dialog.setMessage("短信验证码失败或过期");
									break;
								case 0:
								default:
									dialog.setMessage(error);
									break;
								}
								dialog.create().show();
							}
						});
				break;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 单选框的监听事件，套餐选择
	 */
	private void setRadioGroupListener() {
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radiobutton1:
					packageType = APPConstant.ProdID.RMG_PACKAGE_TYPE_1.getValue();
					switch (type) {
					case TYPE_CREATE_RICH_MAN_GROUP:
						packageIntroduce
								.setText("您是否确认作为发起人开通10元套餐(100M)共享流量套餐，首次开通24小时内生效？");
						break;
					case TYPE_INVITE_MEMBER:
						packageIntroduce
								.setText("您是否确认作为参与者开通10元套餐(100M)共享流量套餐，首次开通24小时内生效？");
						break;
					default :break;
					}
					break;
				case R.id.radiobutton2:
					packageType = APPConstant.ProdID.RMG_PACKAGE_TYPE_2.getValue();
					switch (type) {
					case TYPE_CREATE_RICH_MAN_GROUP:
						packageIntroduce
								.setText("您是否确认作为发起人开通20元套餐(300M)共享流量套餐，首次开通24小时内生效？");
						break;
					case TYPE_INVITE_MEMBER:
						packageIntroduce
								.setText("您是否确认作为参与者开通20元套餐(300M)共享流量套餐，首次开通24小时内生效？");
						break;
						default :break;
					}
					break;
				}
			}
		});
	}
}
