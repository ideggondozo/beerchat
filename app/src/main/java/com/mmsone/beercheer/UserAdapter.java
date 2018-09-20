package  com.mmsone.beercheer;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private final boolean[] checkBoxes;
    private List<User> selectedUsers = new ArrayList<>();
    private Callback mCallback;


    public UserAdapter(List<User> users, Callback callback) {
        this.users = users;
        checkBoxes = new boolean[users.size()];
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        holder.profession.setText(users.get(position).getProfession());

        User user = users.get(position);
        holder.name.setText(users.get(position).getName());

        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if(selectedUsers.contains(user)){
                    selectedUsers.remove(user);
                    holder.selectButton.setText("Select");
                }
                else{
                    selectedUsers.add(user);
                    holder.selectButton.setText("Selected");
                }
            }
        });
        mCallback.onSucces(selectedUsers);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView profession;

        private Button selectButton ;

        public UserViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.txt_name);
            this.profession = itemView.findViewById(R.id.txt_profession);

            this.selectButton =(Button) itemView.findViewById(R.id.addButton);
        }


    }


}
