package com.rarnu.tools.root.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Loader;
import android.content.Loader.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.rarnu.tools.root.R;
import com.rarnu.tools.root.adapter.RecommandAdapter;
import com.rarnu.tools.root.api.LogApi;
import com.rarnu.tools.root.base.BaseFragment;
import com.rarnu.tools.root.common.RecommandInfo;
import com.rarnu.tools.root.comp.DataProgressBar;
import com.rarnu.tools.root.loader.RecommandLoader;
import com.rarnu.tools.root.utils.ApkUtils;

public class RecommandFragment extends BaseFragment implements
		OnLoadCompleteListener<List<RecommandInfo>>, OnItemClickListener {

	ListView lvRecommand;
	DataProgressBar progressRecommand;

	List<RecommandInfo> lstRecommand = new ArrayList<RecommandInfo>();
	RecommandAdapter adapter = null;

	RecommandLoader loader = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogApi.logEnterAppRecommand();
	}

	@Override
	protected int getBarTitle() {
		return R.string.short_recommand;
	}

	@Override
	protected int getBarTitleWithPath() {
		return R.string.short_recommand_with_path;
	}

	@Override
	protected void initComponents() {
		lvRecommand = (ListView) innerView.findViewById(R.id.lvRecommand);
		progressRecommand = (DataProgressBar) innerView
				.findViewById(R.id.progressRecommand);
		adapter = new RecommandAdapter(getActivity(), lstRecommand);
		lvRecommand.setAdapter(adapter);

		lvRecommand.setOnItemClickListener(this);

		loader = new RecommandLoader(getActivity());
		loader.registerListener(0, this);
	}

	@Override
	protected void initLogic() {
		doStartLoad();
	}

	@Override
	protected int getFragmentLayoutResId() {
		return R.layout.layout_recommand;
	}

	@Override
	protected void initMenu(Menu menu) {

	}

	private void doStartLoad() {
		progressRecommand.setAppName(getString(R.string.loading));
		progressRecommand.setVisibility(View.VISIBLE);
		loader.startLoading();
	}

	@Override
	public void onLoadComplete(Loader<List<RecommandInfo>> loader,
			List<RecommandInfo> data) {
		lstRecommand.clear();
		if (data != null) {
			lstRecommand.addAll(data);
		}
		adapter.setNewData(lstRecommand);
		progressRecommand.setVisibility(View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		RecommandInfo item = lstRecommand.get(position);
		ApkUtils.gotoApp(getActivity(), item.packageName, item.mainActivity);

	}

}
