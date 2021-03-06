package com.rarnu.tools.root.service;

import android.app.Notification;
import android.content.Intent;

import com.rarnu.tools.root.api.LogApi;
import com.rarnu.tools.root.base.BaseService;
import com.rarnu.tools.root.common.Actions;
import com.rarnu.tools.root.utils.ApkUtils;

public class CleanBackupService extends BaseService {

	private Intent inCleanBackup = new Intent(Actions.ACTION_CLEANING_BACKUP);

	@Override
	public void initIntent() {
		inCleanBackup.putExtra("operating", true);

	}

	@Override
	public void fiIntent() {
		inCleanBackup.removeExtra("operating");
		inCleanBackup.putExtra("operating", false);
	}

	@Override
	public Intent getSendIntent() {
		return inCleanBackup;
	}

	@Override
	public void doOperation(String command, Notification n) {
		LogApi.logDeleteAllData();
		ApkUtils.deleteAllBackupData();
	}

	@Override
	public boolean getCommandCondition(String command) {
		return command.equals("clean-backup");
	}

}
