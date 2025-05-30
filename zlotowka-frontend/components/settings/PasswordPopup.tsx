import GenericPopup from "@/components/general/GenericPopup";
import { useState } from "react";

interface PasswordPopupProps {
    onCloseAction: () => void;
    onSave: (value: string, fieldName: string) => void;
}

export default function PasswordPopup({ onCloseAction, onSave }: PasswordPopupProps) {
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmNewPassword, setConfirmNewPassword] = useState("");

    const inputClass = "border border-neutral-300 rounded px-4 py-2 mb-3 w-full";

    const handleSave = () => {
        onSave(`${oldPassword}::${newPassword}::${confirmNewPassword}`, "password");
    };

    return (
        <GenericPopup
            title="Zmień hasło"
            onCloseAction={onCloseAction}
            onConfirmAction={handleSave}
        >
            <div className="flex flex-col">
                <input
                    type="password"
                    className={inputClass}
                    placeholder="Stare hasło"
                    value={oldPassword}
                    onChange={(e) => setOldPassword(e.target.value)}
                />
                <input
                    type="password"
                    className={inputClass}
                    placeholder="Nowe hasło"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                />
                <input
                    type="password"
                    className={inputClass}
                    placeholder="Potwierdź nowe hasło"
                    value={confirmNewPassword}
                    onChange={(e) => setConfirmNewPassword(e.target.value)}
                />
            </div>
        </GenericPopup>
    );
}
