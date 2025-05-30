import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";
import { UserDetailsRequest } from "@/interfaces/settings/Settings";

class UserResponse {}

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
    );
  }

  interface PasswordChangePayload {
      oldPassword: string;
      newPassword: string;
      confirmNewPassword: string;
  }

  async function changePassword(payload: PasswordChangePayload) {
      return await sendToBackend(`user/password`, {
          ...withAuthHeader,
          method: "PUT",
          body: JSON.stringify(payload),
      });
  }


  return {
    updateUserDetails,changePassword,
  };
}
