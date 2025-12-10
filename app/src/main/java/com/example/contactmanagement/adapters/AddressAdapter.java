package com.example.contactmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contactmanagement.R;
import com.example.contactmanagement.models.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<Address> addresses = new ArrayList<>();
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onEditClick(Address address);
        void onDeleteClick(Address address);
    }

    public AddressAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address address = addresses.get(position);
        holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStreet, tvCity, tvProvince, tvCountry, tvPostalCode;
        Button btnEditAddress, btnDeleteAddress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStreet = itemView.findViewById(R.id.tvStreet);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvProvince = itemView.findViewById(R.id.tvProvince);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvPostalCode = itemView.findViewById(R.id.tvPostalCode);
            btnEditAddress = itemView.findViewById(R.id.btnEditAddress);
            btnDeleteAddress = itemView.findViewById(R.id.btnDeleteAddress);
        }

        void bind(Address address) {
            tvStreet.setText(address.street);
            tvCity.setText(address.city);
            tvProvince.setText(address.province);
            tvCountry.setText(address.country);
            tvPostalCode.setText(address.postalCode);

            btnEditAddress.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(address);
                }
            });

            btnDeleteAddress.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(address);
                }
            });
        }
    }
}