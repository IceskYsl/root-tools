package com.rarnu.tools.root.fragment;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rarnu.tools.root.GlobalInstance;
import com.rarnu.tools.root.R;
import com.rarnu.tools.root.api.LogApi;
import com.rarnu.tools.root.base.BaseDialogFragment;
import com.rarnu.tools.root.utils.MemorySpecialList;
import com.rarnu.tools.root.utils.MemoryUtils;

public class MemProcessFragment extends BaseDialogFragment implements
		OnClickListener {

	ImageView ImgIcon;
	TextView tvName, tvNamespace, tvWarning;
	TextView tvPidValue, tvMemoryValue, tvUserValue;
	Button btnCancel, btnKill, btnIgnore;

	@Override
	protected void initComponents() {
		ImgIcon = (ImageView) innerView.findViewById(R.id.ImgIcon);
		tvName = (TextView) innerView.findViewById(R.id.tvName);
		tvNamespace = (TextView) innerView.findViewById(R.id.tvNamespace);
		tvWarning = (TextView) innerView.findViewById(R.id.tvWarning);
		tvPidValue = (TextView) innerView.findViewById(R.id.tvPidValue);
		tvMemoryValue = (TextView) innerView.findViewById(R.id.tvMemoryValue);
		tvUserValue = (TextView) innerView.findViewById(R.id.tvUserValue);
		btnCancel = (Button) innerView.findViewById(R.id.btnCancel);
		btnKill = (Button) innerView.findViewById(R.id.btnKill);
		btnIgnore = (Button) innerView.findViewById(R.id.btnIgnore);

		btnCancel.setOnClickListener(this);
		btnKill.setOnClickListener(this);
		btnIgnore.setOnClickListener(this);

	}

	@Override
	protected void initLogic() {
		showProcessInfo();
		showIgnoreStatus();

	}

	@Override
	protected int getFragmentLayoutResId() {
		return R.layout.layout_mem_process;
	}

	private void showIgnoreStatus() {
		int inIgnore = MemorySpecialList
				.inExcludeList(GlobalInstance.currentMemoryProcess.NAME);
		btnIgnore.setText(inIgnore != -1 ? R.string.remove_ignore
				: R.string.add_ignore);
	}

	private void showProcessInfo() {
		if (GlobalInstance.currentMemoryProcess.appInfo == null) {
			ImgIcon.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.android));
			tvName.setText(GlobalInstance.currentMemoryProcess.NAME);
			tvNamespace.setText("");
		} else {
			ImgIcon.setBackgroundDrawable(GlobalInstance.pm
					.getApplicationIcon(GlobalInstance.currentMemoryProcess.appInfo));
			tvName.setText(GlobalInstance.pm
					.getApplicationLabel(GlobalInstance.currentMemoryProcess.appInfo));
			tvNamespace.setText(GlobalInstance.currentMemoryProcess.NAME);
		}

		tvPidValue.setText(String
				.valueOf(GlobalInstance.currentMemoryProcess.PID));
		tvMemoryValue.setText(String.format("%dM",
				GlobalInstance.currentMemoryProcess.RSS));
		tvUserValue.setText(GlobalInstance.currentMemoryProcess.USER);

		// tvWarning.setVisibility(GlobalInstance.currentMemoryProcess.USER.startsWith("app_")
		// ? View.GONE : View.VISIBLE);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnKill:
			if (MemorySpecialList
					.isExcludeLocked(GlobalInstance.currentMemoryProcess.NAME)) {
				Toast.makeText(getActivity(), R.string.locked_app_kill,
						Toast.LENGTH_LONG).show();
				return;
			}
			LogApi.logKillProcess(GlobalInstance.currentMemoryProcess.NAME);
			MemoryUtils.killProcess(GlobalInstance.currentMemoryProcess.PID);

			getActivity().setResult(Activity.RESULT_OK);
			getActivity().finish();
			break;
		case R.id.btnCancel:
			getActivity().finish();
			break;
		case R.id.btnIgnore:
			// add or remove ignore
			int inIgnore = MemorySpecialList
					.inExcludeList(GlobalInstance.currentMemoryProcess.NAME);
			if (inIgnore != -1) {
				// remove ignore
				if (MemorySpecialList
						.isExcludeLocked(GlobalInstance.currentMemoryProcess.NAME)) {
					Toast.makeText(getActivity(), R.string.locked_app_error,
							Toast.LENGTH_LONG).show();
				} else {
					LogApi.logUnignoreProcess(GlobalInstance.currentMemoryProcess.NAME);
					MemorySpecialList
							.removeExclude(GlobalInstance.currentMemoryProcess.NAME);
					if (MemorySpecialList.saveExclude()) {
						Toast.makeText(getActivity(), R.string.added_ignore,
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getActivity(),
								R.string.added_ignore_error, Toast.LENGTH_LONG)
								.show();
					}
				}
			} else {
				// add ignore
				LogApi.logIgnoreProcess(GlobalInstance.currentMemoryProcess.NAME);
				MemorySpecialList
						.addExclude(GlobalInstance.currentMemoryProcess.NAME);
				if (MemorySpecialList.saveExclude()) {
					Toast.makeText(getActivity(), R.string.added_ignore,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity(), R.string.added_ignore_error,
							Toast.LENGTH_LONG).show();
				}
			}
			showIgnoreStatus();
			break;
		}

	}

}
