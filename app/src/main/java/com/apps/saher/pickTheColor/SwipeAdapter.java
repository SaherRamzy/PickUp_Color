package com.apps.saher.pickTheColor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saher on 5/10/2016.
 */
public class SwipeAdapter extends RecyclerSwipeAdapter<SwipeAdapter.SimpleViewHolder> {
    DataBaseManager db;
    MainActivity mainActivity;
    Context mContext;
    ArrayList<Colors> colors;

    public SwipeAdapter(Context context, ArrayList<Colors> objects) {
        this.mContext = context;
        this.colors = objects;
        db = new DataBaseManager(context);
        mainActivity = new MainActivity();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
//        mainActivity.fontChanger.replaceFonts((ViewGroup)view);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder( final SimpleViewHolder colorViewHolder, final int position) {

        colorViewHolder.HEXcolor.setText(colors.get(position).HEXcolor);
        colorViewHolder.RGBcolor.setText(colors.get(position).RGBcolor);
        // pass from XML

        GradientDrawable backgroundGradient = (GradientDrawable) colorViewHolder.gd.getBackground();
        backgroundGradient.setColor((int) Long.parseLong((colors.get(position).HEXcolor.replace("#", "FF")), 16));

//        colorViewHolder.gd.setBackgroundColor((int) Long.parseLong((colors.get(position).HEXcolor.replace("#", "FF")), 16));

//        colorViewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // Drag From Left
//        colorViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, colorViewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        // Drag From Right
        colorViewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, colorViewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));

        // Handling different events when swiping
//        colorViewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
//            @Override
//            public void onClose(SwipeLayout layout) {
//                //when the SurfaceView totally cover the BottomView.
//            }
//
//            @Override
//            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
//                //you are swiping.
//            }
//
//            @Override
//            public void onStartOpen(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onOpen(SwipeLayout layout) {
//                //when the BottomView totally show.
//            }
//
//            @Override
//            public void onStartClose(SwipeLayout layout) {
//
//            }
//
//            @Override
//            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
//                //when user's hand released.
//            }
//        });

        colorViewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, " onClick : " + item.getName() + " \n" + item.getEmailId(), Toast.LENGTH_SHORT).show();
            }
        });

        colorViewHolder.tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharechooser(position);
            }
        });

        colorViewHolder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(colorViewHolder.swipeLayout);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, colors.size());
                db.delete(colors.get(position).ID);
                Log.e("ID from adapter", "" +colors.get(position).ID);
                colors.remove(position);
//                colorViewHolder.setIsRecyclable(false);
                notifyDataSetChanged();
//                Log.e("the delete operation","="+ db.delete(position));
                mItemManger.closeAllItems();
            }
        });

        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(colorViewHolder.itemView, position);

    }
    public void sharechooser(int position){
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.setType("text/plain");

        PackageManager pm = mContext.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");

        Intent openInChooser = Intent.createChooser(emailIntent, "Share via");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if(packageName.contains("mms") || packageName.contains("whatsapp") || packageName.contains("com.facebook.orca") || packageName.contains("com.google.android.apps.docs")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, colors.get(position).HEXcolor +"\n"+ colors.get(position).RGBcolor);
//                if(packageName.contains("whatsapp")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colors.get(position).HEXcolor +"\n"+ colors.get(position).RGBcolor);
//                }
//                else if(packageName.contains("mms")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colors.get(position).HEXcolor +"\n"+ colors.get(position).RGBcolor);
//                }
//                else if(packageName.contains("com.facebook.orca")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, colors.get(position).HEXcolor +"\n"+ colors.get(position).RGBcolor);
//                }
//                else if(packageName.contains("com.google.android.apps.docs")) {
//                    intent.putExtra(Intent.EXTRA_TEXT,colors.get(position).HEXcolor +"\n"+ colors.get(position).RGBcolor);
//                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        mContext.startActivity(openInChooser);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipelayout_1;
    }


    //  ViewHolder Class
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tv_share,tv_delete;
        CardView cv;
        TextView HEXcolor;
        TextView RGBcolor;
        public View gd;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipelayout_1);
            cv = (CardView) itemView.findViewById(R.id.cardview);
            HEXcolor = (TextView) itemView.findViewById(R.id.HEX_color);
            RGBcolor = (TextView) itemView.findViewById(R.id.RGB_color);
            tv_share = (TextView) itemView.findViewById(R.id.tvShare);
            tv_delete = (TextView) itemView.findViewById(R.id.tvDelete);
            gd = itemView.findViewById(R.id.cirle_popup);
        }
    }

}

