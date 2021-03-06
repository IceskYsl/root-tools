package com.rarnu.tools.root.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rarnu.tools.root.R;
import com.rarnu.tools.root.adapter.CacheAdapter;
import com.rarnu.tools.root.api.LogApi;
import com.rarnu.tools.root.base.BaseFragment;
import com.rarnu.tools.root.common.CacheInfo;
import com.rarnu.tools.root.common.MenuItemIds;
import com.rarnu.tools.root.comp.AlertDialogEx;
import com.rarnu.tools.root.comp.DataProgressBar;
import com.rarnu.tools.root.loader.CleanCacheLoader;
import com.rarnu.tools.root.utils.CacheUtils;

public class CleanCacheFragment extends BaseFragment implements
		OnItemLongClickListener, OnLoadCompleteListener<List<CacheInfo>>,
		OnQueryTextListener {

	TextView tvCacheInfo;
	ListView lvCache;
	DataProgressBar progressCache;

	List<CacheInfo> listCacheAll = new ArrayList<CacheInfo>();
	CacheAdapter adapterCache;

	CleanCacheLoader loader = null;

	MenuItem menuRefresh, menuClean;

	@Override
	protected int getBarTitle() {
		return R.string.func6_title;
	}

	@Override
	protected int getBarTitleWithPath() {
		return R.string.func6_title_with_path;
	}

	@Override
	protected void initComponents() {
		lvCache = (ListView) innerView.findViewById(R.id.lvCache);
		progressCache = (DataProgressBar) innerView
				.findViewById(R.id.progressCache);
		tvCacheInfo = (TextView) innerView.findViewById(R.id.tvCacheInfo);

		adapterCache = new CacheAdapter(getActivity().getLayoutInflater(),
				listCacheAll);
		lvCache.setAdapter(adapterCache);
		lvCache.setOnItemLongClickListener(this);

		loader = new CleanCacheLoader(getActivity());
		loader.registerListener(0, this);
	}

	@Override
	protected int getFragmentLayoutResId() {
		return R.layout.layout_clean_cache;
	}

	@Override
	protected void initMenu(Menu menu) {
		MenuItem itemSearch = menu.add(0, MenuItemIds.MENU_SEARCH, 98,
				R.string.search);
		itemSearch.setIcon(android.R.drawable.ic_menu_search);
		itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SearchView sv = new SearchView(getActivity());
		sv.setOnQueryTextListener(this);
		itemSearch.setActionView(sv);

		menuRefresh = menu.add(0, MenuItemIds.MENU_REFRESH, 99,
				R.string.refresh);
		menuRefresh.setIcon(android.R.drawable.ic_menu_revert);
		menuRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		menuClean = menu.add(0, MenuItemIds.MENU_CLEAN, 100, R.string.clean);
		menuClean.setIcon(android.R.drawable.ic_menu_delete);
		menuClean.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MenuItemIds.MENU_REFRESH:
			doStartLoad();
			break;
		case MenuItemIds.MENU_CLEAN:
			doCleanCache();
			break;
		}
		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final CacheInfo info = listCacheAll.get(position);
		AlertDialogEx.showAlertDialogEx(getActivity(),
				getString(R.string.func6_title),
				getString(R.string.confirm_clean_cache),
				getString(R.string.ok),
				new AlertDialogEx.DialogButtonClickListener() {

					@Override
					public void onClick(View v) {
						boolean ret = CacheUtils.cleanCache(info);
						if (ret) {
							adapterCache.deleteItem(info);
							loadCacheCount();
						} else {
							Toast.makeText(getActivity(),
									R.string.clean_cache_failed,
									Toast.LENGTH_LONG).show();
						}

					}
				}, getString(R.string.cancel), null);
		return false;
	}

	private void loadCacheCount() {
		String cacheCount = CacheUtils.countCache(listCacheAll);
		if (this.isAdded()) {
			if (tvCacheInfo != null) {
				tvCacheInfo.setText(String.format(
						getString(R.string.used_cache_size), cacheCount));
			}
		}
	}

	protected void doStartLoad() {
		if (menuClean != null) {
			menuClean.setEnabled(false);
			menuRefresh.setEnabled(false);
		}

		progressCache.setAppName(getString(R.string.loading));
		progressCache.setVisibility(View.VISIBLE);
		loader.startLoading();
	}

	private void doCleanCache() {
		LogApi.logCleanCache();
		if (menuClean != null) {
			menuClean.setEnabled(false);
			menuRefresh.setEnabled(false);
		}
		progressCache.setAppName(getString(R.string.cleaning_cache));
		progressCache.setVisibility(View.VISIBLE);

		final Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {

					Toast.makeText(
							getActivity(),
							(msg.arg1 == 0 ? R.string.clean_all_cache_fail
									: R.string.clean_all_cache_succ),
							Toast.LENGTH_LONG).show();

					doStartLoad();
				}
				super.handleMessage(msg);
			}
		};

		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean ret = CacheUtils.cleanAllCache();
				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = (ret ? 1 : 0);
				h.sendMessage(msg);

			}
		}).start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogApi.logEnterCache();
	}

	@Override
	public void onLoadComplete(Loader<List<CacheInfo>> loader,
			List<CacheInfo> data) {
		listCacheAll.clear();
		if (data != null) {
			listCacheAll.addAll(data);
		}
		adapterCache.setNewList(listCacheAll);
		progressCache.setVisibility(View.GONE);
		if (menuClean != null) {

			menuClean.setEnabled(true);
			menuRefresh.setEnabled(true);
		}
		loadCacheCount();

	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (adapterCache != null) {
			adapterCache.getFilter().filter(newText);
		}
		return true;
	}

	@Override
	protected void initLogic() {
		doStartLoad();
	}

}
