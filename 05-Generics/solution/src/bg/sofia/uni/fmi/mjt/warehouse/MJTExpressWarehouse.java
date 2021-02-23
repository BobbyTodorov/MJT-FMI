package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exceptions.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exceptions.ParcelNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public final class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {

    private final int STORAGE_CAPACITY;

    private Map<L, Pair<P, LocalDateTime>> storage;
    private int storageSize = 0;
    private int retentionPeriod;



    public MJTExpressWarehouse(final int capacity, final int retentionPeriod) {
        if (capacity <= 0 || retentionPeriod <= 0) {
            throw new IllegalArgumentException("arguments must be positive ints");
        }
        this.STORAGE_CAPACITY = capacity;
        this.storage = new LinkedHashMap<>(getStorageCapacity());
        setRetentionPeriod(retentionPeriod);
    }


    public void setRetentionPeriod(final int retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }


    public int getRetentionPeriod() {
        return retentionPeriod;
    }

    public int getStorageCapacity() {
        return STORAGE_CAPACITY;
    }

    public int getStorageSize() {
        return storageSize;
    }


    /**
     * @return true  if storage was cleaned successfully
     * (at least 1 item was removed)
     * and false if storage is still full
     */
    private boolean canCleanStorage() {
        Iterator<L> it = storage.keySet().iterator();
        while (it.hasNext()) {
            L elem = it.next();
            if (Duration.between(storage.get(elem).getRightElement(), LocalDateTime.now()).toDays() > getRetentionPeriod()) {
                storage.remove(elem);
                storageSize--;
            }
        }

        return storageSize < getStorageCapacity();
    }

    private Map<L, P> deliverElementsAccordingToDate(final LocalDateTime date, final boolean isAfter) {
        if (date == null) {
            throw new IllegalArgumentException("LocalDateTime argument must not be null");
        }
        List<L> toRemove = new ArrayList<>();
        Map<L, P> result = new LinkedHashMap<>();
        for (Map.Entry<L, Pair<P, LocalDateTime>> parcel : storage.entrySet()) {
            if ((isAfter && parcel.getValue().getRightElement().isAfter(date))
            || (!isAfter && parcel.getValue().getRightElement().isBefore(date))) {
                result.put(parcel.getKey(), parcel.getValue().getLeftElement());
                toRemove.add(parcel.getKey());
            }
        }

        for (L label : toRemove) {
            storage.remove(label);
            storageSize--;
        }

        return result;
    }

    @Override
    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {
        if (getStorageSize() >= getStorageCapacity() && !canCleanStorage()) {
            throw new CapacityExceededException("Warehouse full capacity.");
        }
        if (submissionDate == null || submissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("given submissionDate must be a non-null date before now()");
        }
        if (label == null || parcel == null) {
            throw new IllegalArgumentException("submitParcel arguments must not be null");
        }

        Pair<P, LocalDateTime> newParcel = new Pair<>(parcel, submissionDate);
        storage.put(label, newParcel);
        storageSize++;
    }

    @Override
    public P getParcel(L label) {
        if (label == null) {
            throw new IllegalArgumentException("given label must not be null");
        }

        Pair<P, LocalDateTime> desiredPair = storage.get(label);

        return desiredPair == null ? null : desiredPair.getLeftElement();
    }

    @Override
    public P deliverParcel(L label) throws ParcelNotFoundException {
        if (label == null) {
            throw new IllegalArgumentException("given label must not be null");
        }
        P parcelToDeliver = getParcel(label);
        if (parcelToDeliver == null) {
            throw new ParcelNotFoundException("parcel with such label was not found");
        }

        storageSize--;
        return storage.remove(label).getLeftElement();
    }

    @Override
    public double getWarehouseSpaceLeft() {
        return 1.0 - Math.floor(((storageSize / (double) getStorageCapacity())) * 100.0) / 100.0;
    }

    @Override
    public Map<L, P> getWarehouseItems() {
        Map<L, P> result = new LinkedHashMap<>();
        for (Map.Entry<L, Pair<P, LocalDateTime>> parcel : storage.entrySet()) {
            result.put(parcel.getKey(), parcel.getValue().getLeftElement());
        }

        return result;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        if (before == null) {
            throw new IllegalArgumentException("before must not be null");
        }

        return deliverElementsAccordingToDate(before, false);
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        if (after == null) {
            throw new IllegalArgumentException("after must not be null");
        }

        return deliverElementsAccordingToDate(after, true);
    }
}
