import {useAuth} from "@/components/providers/AuthProvider";
import sendToBackend, {getAuthHeader} from "@/lib/sendToBackend";
import {UserDetailsRequest} from "@/interfaces/settings/Settings";

class UserResponse {
}

export function useSettingsService() {
  const { token } = useAuth();
  const withAuthHeader = getAuthHeader(token);
  if (!token) throw new Error("User Logged Out (Token not provided)!");


  async function updateUserDetails(
      details: UserDetailsRequest
  ): Promise<UserResponse> {
    return await sendToBackend(
        `user/user-details`,
        {
          ...withAuthHeader,
          method: "PUT",
          body: JSON.stringify(details),
        },
        "Failed to update user details"
    );
  }
  return {
    updateUserDetails,
  };
}