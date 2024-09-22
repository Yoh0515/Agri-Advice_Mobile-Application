package com.example.finalthesis.Marketplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalthesis.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private ArrayList<CartItem> cartItems;
    private Context context;
    private Map<String, Integer> titleCountMap;
    private DatabaseReference cartRef;
    private OnSelectionChangedListener selectionChangedListener;

    public CartAdapter(ArrayList<CartItem> cartItems, Context context, OnSelectionChangedListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        titleCountMap = new HashMap<>();
        this.selectionChangedListener = listener;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(userId);
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    public void setTitleCountMap(Map<String, Integer> titleCountMap) {
        this.titleCountMap = titleCountMap;
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
        void onPriceChanged(double total);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        // Load the first image from the mProduct list
        if (cartItem.getImage() != null && !cartItem.getImage().isEmpty()) {
            Glide.with(context).load(cartItem.getImage().get(0)).into(holder.mImage);
        }
        holder.mTitle.setText(cartItems.get(position).getTitle());
        holder.mPrice.setText(cartItems.get(position).getPrice());
        holder.mUnit.setText(cartItems.get(position).getUnit());
        holder.mName.setText(cartItems.get(position).getVendor());
        holder.qty.setText(cartItems.get(position).getQuantity());

        // Fetch and display the correct cart count
        cartRef.child(cartItem.getKey()).child("cartCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer count = snapshot.getValue(Integer.class);
                if (count != null) {
                    cartItem.setCartCount(count);
                    holder.mNumber.setText("" + count + "   ");
                } else {
                    holder.mNumber.setText("0   ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.mNumber.setText("0   ");
            }
        });

        // Add button click listener
        holder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference itemRef = cartRef.child(cartItem.getKey());
                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int availableQuantity = Integer.parseInt(snapshot.child("quantity").getValue(String.class));
                        int currentCartCount = snapshot.child("cartCount").getValue(Integer.class);
                        int newCartCount = currentCartCount + 1;

                        // Check if the new cart count exceeds the available quantity
                        if (newCartCount > availableQuantity) {
                            Toast.makeText(context, "Cannot add more, out of stock", Toast.LENGTH_SHORT).show();
                        } else {
                            itemRef.child("cartCount").setValue(newCartCount)
                                    .addOnSuccessListener(aVoid -> {
                                        cartItem.setCartCount(newCartCount);
                                        holder.mNumber.setText("" + newCartCount + "   ");
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to update cart", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to check cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Minus button click listener
        holder.mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = cartItem.getCartCount();
                if (count > 0) {
                    count--;
                    cartItem.setCartCount(count);
                    holder.mNumber.setText("" + count + "   ");
                    updateCartCount(cartItem);
                }
            }
        });

        // Detach the listener before setting the checkbox state
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(cartItems.get(position).isSelected());

        // Reattach the listener after setting the checkbox state
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cartItems.get(position).setSelected(isChecked);
                if (selectionChangedListener != null) {
                    selectionChangedListener.onSelectionChanged(getSelectedItemCount());

                    double total = calculateTotalPrice();
                    selectionChangedListener.onPriceChanged(total);
                }
            }
        });
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public double calculateTotalPriceOfSelectedItems() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                totalPrice += Double.parseDouble(item.getPrice()) * item.getCartCount();
            }
        }
        return totalPrice;
    }

    private double calculateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                totalPrice += Double.parseDouble(item.getPrice()) * item.getCartCount();
            }
        }
        return totalPrice;
    }

    private int getSelectedItemCount() {
        int selectedCount = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedCount++;
            }
        }
        return selectedCount;
    }

    public void setAllCheckBoxes(boolean isChecked) {
        for (CartItem item : cartItems) {
            item.setSelected(isChecked);
        }
        notifyDataSetChanged();
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(getSelectedItemCount());
        }
    }

    private void updateCartCount(CartItem item) {
        DatabaseReference itemRef = cartRef.child(item.getKey());
        if (item.getCartCount() > 0) {
            itemRef.child("cartCount").setValue(item.getCartCount());
        } else {
            itemRef.removeValue();
            cartItems.remove(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void searchDataList(ArrayList<CartItem> searchList) {
        cartItems = searchList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mTitle, mPrice, mUnit, mName, mAdd, mMinus, mNumber, qty;
        RelativeLayout mCard;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox_id);
            mImage = itemView.findViewById(R.id.recycleImage3);
            mUnit = itemView.findViewById(R.id.kiloM);
            mTitle = itemView.findViewById(R.id.title3);
            mPrice = itemView.findViewById(R.id.price3);
            mName = itemView.findViewById(R.id.vNameCart);
            mNumber = itemView.findViewById(R.id.mNumber);
            mAdd = itemView.findViewById(R.id.madd);
            mMinus = itemView.findViewById(R.id.mMinus);
            qty = itemView.findViewById(R.id.quantity);
        }
    }
}
