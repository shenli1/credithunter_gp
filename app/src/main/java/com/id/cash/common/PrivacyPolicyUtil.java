package com.id.cash.common;

/**
 * Created by linchen on 2018/6/9.
 */

public class PrivacyPolicyUtil {
    public static boolean shouldShowPrivacyPolicyDialog() {
        boolean isPrivacyPolicyShown = Preference.getInstance().getIsPrivacyPolicyShown();

        return !isPrivacyPolicyShown;
    }

    public static boolean shouldCollectPrivacyData() {
        boolean isPrivacyPolicyShown = Preference.getInstance().getIsPrivacyPolicyShown();
        return isPrivacyPolicyShown && Preference.getInstance().getIsPrivacyPolicyAccepted();
    }

    public static void setPrivacyPolicy(boolean isAccepted) {
        Preference.getInstance().setIsPrivacyPolicyAccepted(isAccepted);
    }
}

