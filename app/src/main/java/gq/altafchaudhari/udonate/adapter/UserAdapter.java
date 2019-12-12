package gq.altafchaudhari.udonate.adapter;

import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gq.altafchaudhari.udonate.R;
import gq.altafchaudhari.udonate.model.User;
import io.reactivex.annotations.NonNull;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item,null);
        UserViewHolder moviesViewHolder = new UserViewHolder(view);
        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.mSrNo.setText(String.valueOf(position+1));
        holder.mName.setText(user.getName());
        holder.mMobile.setText(user.getPhone());
        holder.mEmail.setText(user.getEmail());
    }

    public User getUserAt(int position){
        User user = userList.get(position);
        user.setId(userList.get(position).getId());
        return user;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{

        public TextView mName;
        public TextView mEmail;
        public TextView mMobile;
        public TextView mSrNo;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mSrNo = itemView.findViewById(R.id.tv_srNo);
            mName = itemView.findViewById(R.id.tv_name);
            mMobile = itemView.findViewById(R.id.tv_mobile);
            mEmail = itemView.findViewById(R.id.tv_email);
        }
    }
}