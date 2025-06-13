"use client";

import { UserData, useUserService } from "@/services/UserService";
import { useQueryWithToast } from "@/lib/data-grabbers";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { JSX, useEffect, useState } from "react";
import Image from "next/image";
import { AccountSection } from "@/components/settings/AccountSection";
import { PreferencesSection } from "@/components/settings/PreferencesSection";
import { NotificationsSection } from "@/components/settings/NotificationSection";
import {
  AccountOption,
  EditingFieldProps,
  UserDetailsRequest,
} from "@/interfaces/settings/Settings";
import EditFieldPopup from "@/components/settings/EditFieldPopup";
import PasswordPopup from "@/components/settings/PasswordPopup";
import { useSettingsService } from "@/services/SettingsService";
import { createPayload, validateSettings } from "@/lib/utils";
import toast from "react-hot-toast";
import { useAuth } from "@/components/providers/AuthProvider";

const scrollToSection = (id: string) => {
  document.getElementById(id)?.scrollIntoView({ behavior: "smooth" });
};

export default function Settings(): JSX.Element {
  const navLinks = ["Konto", "Preferencje", "Powiadomienia"];
  const userService = useUserService();
  const settingsService = useSettingsService();
  const queryClient = useQueryClient();
  const { setLogout } = useAuth();
  const [darkMode, setDarkMode] = useState<boolean | null>(null);
  const [notificationsByEmail, setNotificationsByEmail] = useState<boolean | null>(null);
  const [notificationsByPhone, setNotificationsByPhone] = useState<boolean | null>(null);
  const [editingField, setEditingField] = useState<EditingFieldProps>({
    isOpen: false,
    fieldName: "",
    title: "",
    value: "",
  });

  const { data } = useQueryWithToast<UserData>({
    queryKey: ["user"],
    queryFn: userService.fetchUserData,
    staleTime: 0,
  });

  // synchronizes user data with those from backend
  useEffect(() => {
    if (data) {
      setDarkMode(data.darkMode);
      setNotificationsByEmail(data.notificationsByEmail);
      setNotificationsByPhone(data.notificationsByPhone);
    }
  }, [data]);

  const mutation = useMutation({
    mutationFn: (details: UserDetailsRequest) =>
      settingsService.updateUserDetails(details),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["user"] });
      toast.success("Udało się zmienić dane!");
    },
    onError: (error: unknown) => {
      const message: string =
        error instanceof Error
          ? error.message
          : "Nie udało się zmienić danych!";
      toast.error(message);
    },
  });

  const changePasswordMutation = useMutation<
      void,
      Error,
      { oldPassword: string; newPassword: string; confirmNewPassword: string }
  >({
    mutationFn: (payload) => settingsService.changePassword(payload),
    onSuccess: () => {
      toast.success("Hasło zostało zmienione! Zaloguj się ponownie.");
      closeEditPopup();
      setTimeout(() => setLogout(), 1000);
    },
    onError: () => {
      toast.error("Nie udało się zmienić hasła");
    },
  });

  const toggleDarkMode = (value: boolean) => {
    setDarkMode(value);
    handleSaveField(String(value), "darkMode");
  };

  const toggleNotificationsByEmail = (value: boolean) => {
    setNotificationsByEmail(value);
    handleSaveField(String(value), "notificationsByEmail");
  };

  const toggleNotificationsByPhone = (value: boolean) => {
    setNotificationsByPhone(value);
    handleSaveField(String(value), "notificationsByPhone");
  };

  const openEditPopup = (
    fieldName: string,
    title: string,
    value: string = "",
  ) => {
    setEditingField({
      isOpen: true,
      fieldName,
      title,
      value,
    });
  };

  const closeEditPopup = () => {
    setEditingField({
      ...editingField,
      isOpen: false,
    });
  };

  const handleSaveField = (value: string, fieldName?: string) => {
    if (!data || !fieldName) return;
    const parsedValue = value === "true";
    const error = validateSettings(value, fieldName);
    if (error) {
      toast.error(error);
      return;
    }
    if (fieldName === "password") {
      const [oldPassword, newPassword, confirmNewPassword] = value.split("::");
      if (!oldPassword || !newPassword || !confirmNewPassword) {
        toast.error("Uzupełnij wszystkie pola hasła");
        return;
      }
      if (newPassword !== confirmNewPassword) {
        toast.error("Nowe hasła nie są zgodne");
        return;
      }
      changePasswordMutation.mutate({ oldPassword, newPassword, confirmNewPassword });
      return;
    }

    const payload = createPayload(
        fieldName,
        value,
        data,
        fieldName === "darkMode" ? parsedValue : darkMode!,
        fieldName === "notificationsByEmail" ? parsedValue : notificationsByEmail!,
        fieldName === "notificationsByPhone" ? parsedValue : notificationsByPhone!
    );

    mutation.mutate(payload);
  };

  const accountOptions: AccountOption[] = [
    {
      text: "Zdjęcie",
      avatar: <Image src="/avatar.png" width={45} height={45} alt="Avatar" className="rounded-full" />,
      fieldName: "avatar",
    },
    {
      text: "Nazwa użytkownika",
      value: data?.firstName
        ? `${data?.firstName} ${data?.lastName}`
        : "Użytkownik...",
      onClick: () =>
        openEditPopup(
          "name",
          "Edytuj nazwę użytkownika",
          data?.firstName ? `${data?.firstName} ${data?.lastName}` : "",
        ),
      fieldName: "name",
    },
    {
      text: "E-mail",
      value: data?.email || "Email...",
      fieldName: "email",
    },
    {
      text: "Hasło",
      placeholder: "********",
      onClick: () => openEditPopup("password", "Zmień hasło"),
      fieldName: "password",
    },
    {
      text: "Numer telefonu",
      value: data?.phoneNumber || "Nie podano...",
      onClick: () =>
        openEditPopup(
          "phoneNumber",
          "Edytuj numer telefonu",
          data?.phoneNumber || "",
        ),
      fieldName: "phoneNumber",
    },
  ];

  return (
    <div className="w-full p-8 min-h-screen xl:px-32 xl:py-20 font-semibold ">
      <div>
        <h1 className="text-4xl">Ustawienia</h1>
      </div>

      <div className="flex flex-col sm:flex-row gap-y-2 sm:gap-x-6 mt-8">
        {navLinks.map((link) => (
          <div
            key={link}
            className="px-6 py-2 rounded-lg hover:cursor-pointer bg-accent hover:bg-veryDark text-background transition-all ease-in-out duration-200"
            onClick={() => scrollToSection(link)}
          >
            <p className="text-lg">{link}</p>
          </div>
        ))}
      </div>

      <AccountSection accountOptions={accountOptions} />
      <PreferencesSection darkMode={darkMode} setDarkMode={toggleDarkMode} />
      <NotificationsSection
        notificationsByEmail={notificationsByEmail}
        setNotificationsByEmail={toggleNotificationsByEmail}
        notificationsByPhone={notificationsByPhone}
        setNotificationsByPhone={toggleNotificationsByPhone}
      />

      {editingField.isOpen && (
        <EditFieldPopup
          onCloseAction={closeEditPopup}
          title={editingField.title}
          fieldName={editingField.fieldName}
          initialValue={editingField.value}
          onSave={handleSaveField}
        />
      )}
      {editingField.isOpen && editingField.fieldName === "password" && (
          <PasswordPopup onCloseAction={closeEditPopup} onSave={handleSaveField} />
      )}
    </div>
  );
}
