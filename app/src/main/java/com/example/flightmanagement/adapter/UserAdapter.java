package com.example.flightmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightmanagement.R;
import com.example.flightmanagement.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
        void onDeleteClick(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnUserClickListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userEmail;
        private TextView userRole;
        private TextView userPhone;
        private TextView deleteButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            userRole = itemView.findViewById(R.id.user_role);
            userPhone = itemView.findViewById(R.id.user_phone);
            deleteButton = itemView.findViewById(R.id.delete_button);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(userList.get(position));
                }
            });

            if (deleteButton != null) {
                deleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onDeleteClick(userList.get(position));
                    }
                });
            }
        }

        public void bind(User user) {
            if (userName != null) {
                userName.setText(user.getName());
            }
            if (userEmail != null) {
                userEmail.setText(user.getEmail());
            }
            if (userRole != null) {
                String role = user.getRole();
                userRole.setText(role != null ? role.toUpperCase() : "USER");
            }
            if (userPhone != null) {
                String phone = user.getPhone();
                userPhone.setText(phone != null ? phone : "N/A");
            }
        }
    }
}
