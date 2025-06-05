import sendToBackend from "@/lib/sendToBackend";

export interface ForgotResponse {
    resetToken: string;
    message: string;
}

export interface ResetResponse {
    message: string;
}

export function usePasswordService() {
    async function forgotPassword(email: string): Promise<ForgotResponse> {
        return sendToBackend(
            `auth/password/forgot`,
            {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email }),
            }
        );
    }

    async function resetPassword(
        email: string,
        token: string,
        newPassword: string
    ): Promise<ResetResponse> {
        return sendToBackend(
            `auth/password/reset`,
            {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, token, newPassword }),
            }
        );
    }

    return { forgotPassword, resetPassword };
}
