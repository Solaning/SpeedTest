package com.kinth.mmspeed.util;

public class BroadcastReceiverAction {
	public static final String ACTION_REPLY_GET_QUESTIONS_LIST = "com.qs.answerking.action_reply_get_questions_list";
	public static final String EXTRA_DATA_REPLY_GET_QUESTION_LIST = "List_qtlist";

	public static final String ACTION_REPLY_GET_CP_QUESTIONS_LIST = "com.qs.answerking.action_reply_get_cp_questions_list";
	public static final String EXTRA_DATA_REPLY_GET_CP_QUESTION_LIST = "List_cp_qtlist";
	/**
	 * �㲥��Ϣ����ȡ���а�Ļظ�
	 */
	public static final String ACTION_REPLY_GET_RANKING_LIST = "com.qs.answerking.action_reply_get_ranking_list";
	public static final String EXTRA_DATA_REPLY_GET_RANKING_LIST_TYPE = "int_type";
	public static final String EXTRA_DATA_REPLY_GET_RANKING_LIST_KEY = "String_key";
	/**
	 * �㲥��Ϣ���޷����ӷ�����
	 */
	// public static final String ACTION_ERROR_DISCONNECTION_TO_SERVER =
	// "com.qs.answerking.action_error_disconnection_to_server";
	/**
	 * �㲥��Ϣ���������ӷ������ɹ�
	 */
	// public static final String ACTION_RECONNECTION_SUCCESS =
	// "com.qs.answerking.action_reconnection_success";
	/**
	 * �㲥��Ϣ����ȡ���Ѷ�̬��Ϣ�б�
	 */
	public static final String ACTION_REPLY_GET_FRIEND_RECENT_STATE_MSG_LIST = "com.qs.answerking.action_reply_get_friend_recent_state_msg_list";
	public static final String EXTRA_DATA_REPLY_GET_FRIEND_RECENT_STATE_MSG_LIST = "List<FriendRecentStateMsg>_list";

	/**
	 * �㲥��Ϣ:���?΢�ź��ѻ��ߺ���Ȧ�Ľ��
	 */
	public static final String ACTION_RESPONSE_SHARE_TO_WEIXIN = "com.qs.answerking.action_response_share_weixin";
	public static final String EXTRA_DATA_RESPONSE_SHARE_TO_WEIXIN = "int_code";

	/**
	 * 
	 */
	public static final String ACTION_HANDLE_INVITE_MSG_WHEN_CLICK_NOTIFICATION = "com.qs.answerking.action_handle_invite_msg_when_click_notification";

	/**
	 * 登录后服务器的返回数据
	 */
	public static final String ACTION_REPLY_LOGIN = "com.qs.answerking.action_repyly_login";
	public static final String EXTRA_DATA_REPLY_LOGIN_RTN = "int_rtn";

	/**
	 * �㲥��Ϣ��˫��PK��Ϸ����һ��
	 */
	public static final String ACTION_REPLY_PK_GM_NEXT_STEP = "com.qs.answerking.action_reply_pk_gm_next_step";
	public static final String EXTRA_DATA_REPLY_PK_GM_NEXT_STEP_RIGHT_OR_WRONG = "int_rightOrWrong";
	public static final String EXTRA_DATA_REPLY_PK_GM_NEXT_STEP_CHOICE = "int_choice";
	public static final String EXTRA_DATA_REPLY_PK_GM_NEXT_STEP_PROP = "int_prop";
	public static final String EXTRA_DATA_REPLY_PK_GM_NEXT_STEP_MYQS = "Question_myqs";
	public static final String EXTRA_DATA_REPLY_PK_GM_NEXT_STEP_UQS = "Question_uqs";

	/**
	 * �㲥��Ϣ���Է�ʹ���ˡ��ߡ��ĵ���
	 */
	public static final String ACTION_REPLY_OPPONENT_USER_PROP_KICK = "com.qs.anserking.action_reply_opponent_user_prop_kick";
	public static final String EXTRA_DATA_REPLY_OPPONENT_USER_PROP_KICK_QS1 = "Question_qs1";
	public static final String EXTRA_DATA_REPLY_OPPONENT_USER_PROP_KICK_QS2 = "Question_qs2";

	/**
	 * �㲥��Ϣ�������������ɹ�
	 */
	public static final String ACTION_RECONNECT_OK = "com.qs.answerking.action_reconnect_ok";
	/**
	 * �㲥��Ϣ������������ʧ��
	 */
	public static final String ACTION_RECONNECT_FAILED = "com.qs.answerking.action_reconnect_failed";
	/**
	 * �㲥��Ϣ����ǰ���粻���ã�������������
	 */
	public static final String ACTION_CONNECTION_UNAVAILABLE = "com.qs.answerking.action_connection_unavailable";
	/**
	 * �㲥��Ϣ������������ʧ��
	 */
	public static final String ACTION_CONNECT_TO_SERVER_FAILED = "com.qs.answerking.action_connect_to_server_failed";
	/**
	 * �㲥��Ϣ������gmChoice�����
	 */
	public static final String ACTION_FINISH_GM_CHOICE_ACTIVITY = "com.qs.answerking.action_finish_gm_choice_activity";
	/**
	 * �㲥��Ϣ�����˳�����
	 */
	public static final String ACTION_FINISH_GAME_NORMALLY = "com.qs.anserking.action_finish_game_normally";
	/**
	 * �㲥��Ϣ������˫��PK���Լ��Ǳ�������
	 */
	public static final String ACTION_OK_CREATE_2PGM_FOR_BEING_INVITED = "com.qs.answerking.action_ok_create_2pgm_for_being_invited";
	public static final String EXTRA_DATA_OK_CREATE_2PGM_FOR_BEING_INVITED = "int_turn";
	/**
	 * �㲥��Ϣ����ȡ�ն˵Ĺ�����Ϣ������mac��ַ���豸��ƣ�UUID��
	 */
	public static final String ACTION_GET_TERMINAL_END_COMMON_INFO = "com.qs.answerking.action_get_terminal_end_common_info";
	/**
	 * �㲥��Ϣ���յ���¼����Ļظ�
	 */
	public static final String ACTION_REPLY_REGISTER = "com.qs.answerking.action_reply_register";
	public static final String EXTRA_DATA_REPLY_REGISTER = "int_rtn";
	/**
	 * �㲥��Ϣ���������˫����Ϸ���ҳ��
	 */
	public static final String ACTION_FINISH_TWO_PGM_FRIEND_RSLT_ACT = "com.qs.answserking.action_finish_two_pgm_friend_rslt_act";
	/**
	 * �㲥��Ϣ��˫��PKʱ�յ��Է��Ļ�����Ϣ
	 */
	public static final String ACTION_GET_GM_PK_INTERACT = "com.qs.answerking.action_get_gm_pk_interact";
	public static final String EXTRA_DATA_GET_GM_PK_INTERACT = "int_id";
	/**
	 * �㲥��Ϣ����ȡ���а�Ĳ�ͬ�����б�
	 */
	public static final String ACTION_REPLY_GET_RANK_TYPE_ITEM_LIST = "com.qs.answerking.action_reply_get_rank_type_item_list";
	/**
	 * �㲥��Ϣ����ȡ��������б� version2.0
	 * 
	 * @author chenbijia
	 */
	public static final String ACTION_GET_POINT_CLASS_LIST = "com.qs.answerking.action_get_point_class_list";
	public static final String EXTRA_DATA_POINT_CLASS_LIST = "list_pointsClasses";
	/**
	 * �㲥��Ϣ����ȡĳ������������ϸ�б� version2.0
	 * 
	 * @author chenbijia
	 */
	public static final String ACTION_GET_POINT_DETAIL_LIST = "com.qs.answerking.action_get_point_detail_list";
	/**
	 * �㲥��Ϣ����������ģʽ�ĵ�����Ϸ version2.0
	 * 
	 * @author chenbijia
	 */
	public static final String ACTION_CREATE_CHALLENGE_GAME = "com.qs.answerking.action_create_challenge_game";
	/**
	 * �㲥��Ϣ����������ģʽ��˫����Ϸ
	 * 
	 * @author linjinbiao
	 */
	public static final String ACTION_REPLY_CREATE_2P_GM_CHALLENGE = "com.qs.answerking.action_reply_create_2p_gm_challenge";
	public static final String EXTRA_DATA_REPLY_CREATE_2P_GM_CHALLENGE = "int_turn";
	/**
	 * �㲥��Ϣ��������ս���ѵĵ�����Ϸ��¼
	 * 
	 * @author linjinbiao
	 */
	public static final String ACTION_REQUEST_CHALLEGE_FRIEND_RECORD = "com.qs.answerking.action_request_challenge_friend_record";
	public static final String EXTRA_DATA_REQUEST_CHALLENGE_FRIEND_RECORD = "FriendRecentStateMsg_msg";
	/**
	 * �㲥��Ϣ����ȡ�Ƽ�¼�ĺ��Ѷ�̬����Ϣ�ظ�
	 */
	public static final String ACTION_REPLY_GET_FRIEND_ACTIVE_EXTRA_INFO_OF_ANSWER_DETAIL = "com.qs.answerking.actin_reply_get_freind_active_extra_info_of_answer_detail";
	public static final String EXTRA_DATA_REPLY_GET_FRIEND_ACTIVE_EXTRA_INFO_OF_ANSWER_DETAIL = "List<AnaswerDetail>_answerDetailList";
	/**
	 * �㲥��Ϣ���ɹ�������ս���Ѽ�¼����Ϸ
	 */
	public static final String ACTION_OK_CREATE_GM_FOR_CHALLENGING_FRIEND_RECORD = "com.qs.answerking.action_ok_create_gm_for_challenging_friend_record";
	public static final String EXTRA_DATA_OK_CREATE_GM_FOR_CHALLENGING_FRIEND_RECORD_QSTYPE = "List<Integer>_qstype";
	public static final String EXTRA_DATA_OK_CREATE_GM_FOR_CHALLENGING_FRIEND_RECORD_QSLIST = "List<Question>_qslist";
	public static final String EXTRA_DATA_OK_CREATE_GM_FOR_CHALLENGING_FRIEND_RECORD_RECORDPOINTS = "int_record_points";
	public static final String EXTRA_DATA_OK_CREATE_GM_FOR_CHALLENGING_FRIEND_RECORD_CLASSNAME = "String_classname";
	/**
	 * �㲥��Ϣ����ȡͨ��ؿ��ĺ����б������ظ�
	 */
	public static final String ACTION_REPLY_GET_PASSING_CP_FRIENDS_LIST = "com.qs.answerking.action_reply_get_passing_cp_friends_list";

	public static final String ACTION_REPLY_IMCHAT = "com.kinth.imchat.action_reply_imchat";
	public static final String EXTRA_DATA_REPLY_IMCHAT = "imlog_rtn";
	
	public static final String ACTION_REPLY_IMCHAT_RESULT = "action_reply_imchat_result";
	public static final String EXTRA_DATA_REPLY_IMCHAT_RESULT = "imlog_turn_result";
	public static final String EXTRA_DATA_REPLY_IMCHAT_TASTID = "imlog_turn_taskid";
	
	
	public static final String ACTION_REPLY_GETUNREDMSG = "action_reply_get_unreadmsg";
	public static final String EXTRA_DATA_ACTION_REPLY_GETUNREDMSG = "extra_data_get_unreadmsg";
/**
 * 全局广播
 */
	public static final String ACTION_RECEIVE_CHATMSG = "action_get_msg";
	public static final String EXTRA_DATA_RECEIVE_CHATMSG = "extra_data_get_msg";
	
	public static final String ACTION_LATEST_CHATMSG = "action_latest_msg";
	public static final String EXTRA_DATA_ACTION_LATEST_CHATMSG = "data_action_latest_msg";
	
	public static final String ACTION_REPLY_CREATE_GROUP = "action_reply_create_group";
	public static final String EXTRA_DATA_REPLY_CREATE_GROUP_RESULT = "extra_data_reply_create_group_result";
	public static final String EXTRA_DATA_REPLY_CREATE_GROUP_GID = "extra_data_reply_create_group_gid";
	
	public static final String ACTION_REPLY_GET_GROUPINFO = "action_reply_get_groupinfo";
	public static final String EXTRA_DATA_REPLY_GET_GROUPINFO = "extra_data_reply_get_groupinfo";

	public static final String ACTION_REPLY_GET_USERINFO = "action_reply_get_userinfo";
	public static final String EXTRA_DATA_REPLY_GET_USERINFO = "extra_data_reply_get_userinfo";
	
	public static final String ACTION_REPLY_DEL_GROUP_USER = "action_reply_del_group_user";
	public static final String EXTRA_DATA_REPLY_DEL_GROUP_USER = "extra_data_reply_del_group_user";

	public static final String ACTION_REPLY_ADD_GROUP_USER = "action_reply_add_group_user";
	public static final String EXTRA_DATA_REPLY_ADD_GROUP_USER = "extra_data_reply_add_group_user";
	
	public static final String ACTION_FINISH_DL_AND_UPDATE_MSG = "action_finish_dl_and_update_msg";
	public static final String EXTRA_DATA_FINISH_DL_AND_UPDATE_MSG = "extra_data_finish_dl_and_update_msg";

	public static final String ACTION_UPDATE_LATEST_CHATMSG_FROM_WECHAT = "UPDATE_MSG_FROM_WECHAT";
	
	public static final String ACTION_REPLY_MODIFY_GROUPNAME = "ACTION_MODIFY_GROUPNAME";
	public static final String EXTRA_DATA_REPLY_MODIFY_GROUPNAME = " EXTRA_DATA_MODIFY_GROUPNAME";
	
}
