//package com.yourwelcome.czirjak.firebasefriends;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.List;
//
//public class Selector extends BaseAdapter {
//    private Context context;
//    private List<User> selectedUsers;
//    private LayoutInflater inflater;
//    private ApiConfiguration apiConfiguration;
//
//    public Selector(Context context, List<User> selectedUsers) {
//        this.context = context;
//        this.selectedUsers = selectedUsers;
//    }
//
//    @Override
//    public int getCount() {
//        return selectedUsers.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return selectedUsers.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (inflater == null) {
//            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//        if (view == null) {
//            view = inflater.inflate(R.layout.feed_item_friends, null);
//        }
//
//        //finding different views
//        CheckBox chkFriends = (CheckBox) view.findViewById(R.id.chkFriends);
//
//        final User user = selectedUsers.get(i);
//        String name = user.getName();
//        apiConfiguration = new ApiConfiguration();
//        String api = apiConfiguration.getApi();
//
//        chkFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                Log.e("Checkboxxxxxxxxxx", "Clicked");
//                if (isChecked) {
//                    user.setSelected(true);
//                    Log.e("Checkbox", "Checked");
//                } else {
//                    user.setSelected(false);
//                    Log.e("Checkbox", "UnChecked");
//                }
//                selectedUsers.add(user);
//            }
//        });
//        return view;
//    }
//}