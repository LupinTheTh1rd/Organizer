package com.example.homepage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PopUp extends Fragment {

    TextView tvcapacityval, tvprojectorval;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        getContext().getTheme().applyStyle("@style/AppTheme.NoActionBar", true);
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(),R.style.AppTheme_PopupOverlay);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.popup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        //NEW
//        tvcapacityval= view.findViewById(R.id.tvcapacityval);
//        tvprojectorval= view.findViewById(R.id.tvprojectorval);
        DisplayMetrics dm=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width=dm.widthPixels;
        int height=dm.heightPixels;

        getActivity().getWindow().setLayout((int)(width*.7),(int)(height*.7));

        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;

        getActivity().getWindow().setAttributes(params);

//        Bundle extras = getIntent().getExtras();
//        String value1 = extras.getString("Value1");
//        String value2 = extras.getString("Value2");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tvcapacityval.setText(bundle.getString("Value1"));
            tvprojectorval.setText(bundle.getString("Value2"));
        }
    }

    public TextView getCapacity() {
        return tvcapacityval;
    }

    public TextView getProjector() {
        return tvprojectorval;
    }
}
