package sssp.Model;

public enum DBKeys {
    FIRST_NAME("FirstName"),
    LAST_NAME("LastName"),
    NOTES("Notes"),
    GUEST_SINCE_DATE("GuestSinceDate"),
    LAST_VISIT_DATE("LastVisitDate"),

    CASE_CHECK("CaseCheck"),
    SLEEPING_PAD_CHECK("SleepingPadCheck"),
    OUTREACH_BACKPACK_CHECK("OutreachBackpackCheck"),
    BACKPACK_CHECK("BackpackCheck"),
    TENT_CHECK("TentCheck"),
    SLEEPING_BAG_CHECK("SleepingBagCheck"),
    HMIS_CHECK("HMISCheck"),

    SLEEPING_BAG_DATE("SleepingBagDate"),
    TENT_DATE("TentDate"),
    BACKPACK_DATE("BackpackDate"),
    OUTREACH_BACKPACK_DATE("OutreachBackpackDate"),
    SLEEPING_PAD_DATE("SleepingPadDate"),

    SM_LOCKER_NUMBER("SmallLockerNumber"),
    SM_LOCKER_START_DATE("SmallLockerStartDate"),
    SM_LOCKER_LAST_ACCESSED_DATE("SmallLockerLastAccessedDate"),
    SM_LOCKER_NOTES("SmallLockerNotes"),
    SM_LOCKER_ASSIGNING_STAFF("SmallLockerAssigningStaff"),

    DAY_STORAGE_SHELF("DayStorageShelf"),
    DAY_STORAGE_SLOT("DayStorageSlot"),
    DAY_STORAGE_START_DATE("DayStorageStartDate"),
    DAY_STORAGE_EXPIRATION_DATE("DayStorageExpirationDate"),
    DAY_STORAGE_CONTAINER_DESCRIPTION("DayStorageDescription"),
    DAY_STORAGE_STAFF_INITIALS("DayStorageStaffInitials"),
    DAY_STORAGE_CONTRACT_CHECK("DayStorageContractCheck"),

    CS_PREVIOUS_LOCATION("CubeStoragePreviousLocation"),
    CS_REASON_FOR_MOVE("CubeStorageReasonForMove"),
    CS_CONTAINER_DESCRIPTION("CubeStorageContainerDescription"),
    CS_START_DATE("CubeStorageStartDate"),
    CS_EXPIRATION_DATE("CubeStorageExpirationDate"),
    CS_GUEST_NOTIFIED_CHECK("CubeStorageGuestNotifiedCheck"),

    MED_LOCKER_NUMBER("MediumLockerNumber"),
    MED_LOCKER_ACCOMMODATION_LINK("MediumLockerAccommodationLink"),
    MED_LOCKER_START_DATE("MediumLockerStartDate"),
    MED_LOCKER_LAST_ACCESSED_DATE("MediumLockerLastAccessedDate"),
    MED_LOCKER_NOTES("MediumLockerNotes"),
    MED_LOCKER_ASSIGNING_STAFF("MediumLockerAssigningStaff");


    private final String key;

    DBKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}