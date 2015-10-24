package com.kinth.mmspeed.ui;

import com.kinth.mmspeed.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 通用对话框
 * @author Sola
 *
 */
public class CommomDialog extends Dialog {

	public CommomDialog(Context context, int theme) {
		super(context, theme);
	}

	public CommomDialog(Context context) {
		super(context);
	}
	
	/**
	 * 设置宽和高
	 * @param width
	 * @param height
	 */
	public CommomDialog setLayout(int width,int height){
		 final WindowManager.LayoutParams attrs = getWindow().getAttributes();  
		 attrs.width = width;
		 attrs.height = height;
		 getWindow().setAttributes(attrs);
		 return this;
	}
	
	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private int left, right, top, bottom;
		private int width,height;//宽和高
		private int buttonId = -1;

		String[] items = null;
		private int titleTextColor = Integer.MAX_VALUE;
		private int buttonTextColor = Integer.MAX_VALUE;
		private int buttonbackgroundId = -1;
		private String positiveButtonText;
		private String negativeButtonText;
		private String neutralButtonText;
		private View contentView;
		private Button postiveButton;
		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener, neutralButtonClickListener,
				itemButtonListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitleTextColor(int titleTextColor) {
			this.titleTextColor = titleTextColor;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setButtonTextColor(int buttonTextColor) {
			this.buttonTextColor = buttonTextColor;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setButtonBackground(int buttonbackgroundId) {
			this.buttonbackgroundId = buttonbackgroundId;
			return this;
		}

		public Builder setButtonTheme(int buttonId) {
			this.buttonId = buttonId;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setView(View v, int left, int top, int right, int bottom) {
			this.contentView = v;
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
			return this;
		}
		
		public Builder setItem(String items[], OnClickListener listener) {
			this.items = items;
			itemButtonListener = listener;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(int neutralButtonText,
				DialogInterface.OnClickListener listener) {
			this.neutralButtonText = (String) context
					.getText(neutralButtonText);
			this.neutralButtonClickListener = listener;
			return this;
		}

		public Builder setNeutralButton(String neutralButtonText,
				DialogInterface.OnClickListener listener) {
			this.neutralButtonText = neutralButtonText;
			this.neutralButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CommomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CommomDialog Mydialog = new CommomDialog(
					context, R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_layout, null);
			postiveButton = ((Button) layout.findViewById(R.id.positiveButton));
			if (titleTextColor != Integer.MAX_VALUE) {
				((TextView) layout.findViewById(R.id.title))
						.setTextColor(titleTextColor);
			}
			if (buttonTextColor != Integer.MAX_VALUE) {
				postiveButton.setTextColor(buttonTextColor);
				((Button) layout.findViewById(R.id.negativeButton))
						.setTextColor(buttonTextColor);
				((Button) layout.findViewById(R.id.neutralButton))
						.setTextColor(buttonTextColor);
			}
			if (buttonId != -1) {
				postiveButton.setBackgroundResource(buttonId);
				((Button) layout.findViewById(R.id.negativeButton))
						.setBackgroundResource(buttonId);
				((Button) layout.findViewById(R.id.neutralButton))
						.setBackgroundResource(buttonId);
			}
			if (buttonbackgroundId != -1) {
				((LinearLayout) layout.findViewById(R.id.mydialogbuttonlayout))
						.setBackgroundResource(buttonbackgroundId);
			}
			Mydialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			if (title != null) {
				((TextView) layout.findViewById(R.id.title)).setText(title);
			} else {
				((LinearLayout) layout.findViewById(R.id.mydiatitlelayout))
						.setVisibility(View.GONE);
			}
			if (positiveButtonText != null) {
				postiveButton.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					postiveButton
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(
											Mydialog,
											DialogInterface.BUTTON_POSITIVE);
									postiveButton.setClickable(false);
									Mydialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}
			if (neutralButtonText != null) {
				((Button) layout.findViewById(R.id.neutralButton))
						.setText(neutralButtonText);
				if (neutralButtonClickListener != null) {
					((Button) layout.findViewById(R.id.neutralButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									neutralButtonClickListener.onClick(
											Mydialog,
											DialogInterface.BUTTON_NEUTRAL);
									Mydialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.neutralButton)
						.setVisibility(View.GONE);
			}
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(
											Mydialog,
											DialogInterface.BUTTON_NEGATIVE);
									Mydialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}
			if (negativeButtonText == null && neutralButtonText == null
					&& positiveButtonText == null) {
				((LinearLayout) layout.findViewById(R.id.mydialogbuttonlayout))
						.setVisibility(View.GONE);
			}
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else {
				((TextView) layout.findViewById(R.id.message))
						.setVisibility(View.GONE);
			}
			if (items != null && items.length > 0) {
				LinearLayout selflayout = ((LinearLayout) layout
						.findViewById(R.id.content));
				selflayout.removeAllViews();
				for (int i = 0; i < items.length; i++) {
					final int pos = i;
					TextView text = new TextView(context);
					text.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT, 80));
					text.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
					text.setTextSize(16);
					text.setTextColor(Color.BLACK);
					text.setBackgroundColor(Color.WHITE);
					text.setPadding(30, 0, 0, 0);
					text.setText(items[i]);
					selflayout.addView(text);
					if (i != items.length - 1) {
						ImageView textline = new ImageView(context);
						textline.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT, 1));
						textline.setImageResource(R.drawable.dialog_divider_line);
						selflayout.addView(textline);
					}
					if (itemButtonListener != null) {
						text.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								itemButtonListener.onClick(Mydialog, pos);
								Mydialog.dismiss();
							}
						});
					}
				}
			} else if (contentView != null) {
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).setPadding(
						left, top, right, bottom);
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
			}
			Mydialog.setContentView(layout);
			return Mydialog;
		}
	}
}
