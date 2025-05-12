"use client";

import {UserData, useUserService} from "@/services/UserService";
import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {JSX, useEffect, useState} from "react";
import Image from "next/image";
import {AccountSection} from "@/components/settings/AccountSection";
import {PreferencesSection} from "@/components/settings/PreferencesSection";
import {NotificationsSection} from "@/components/settings/NotificationSection";
import {AccountOption, EditingFieldProps, UserDetailsRequest} from "@/interfaces/settings/Settings";
import EditFieldPopup from "@/components/settings/EditFieldPopup";
import {useSettingsService} from "@/services/SettingsService";
import {createPayload} from "@/lib/utils";
import toast from "react-hot-toast";

const scrollToSection = (id: string) => {
  document.getElementById(id)?.scrollIntoView({ behavior: "smooth" });
};

export default function Settings(): JSX.Element {
  const navLinks = ["Konto", "Preferencje", "Powiadomienia"];
  const UserService = useUserService();
  const settingsService = useSettingsService();
  const queryClient = useQueryClient();
  const [darkMode, setDarkMode] = useState<boolean | null>(null);
  const [notificationsByEmail, setNotificationsByEmail] = useState<boolean | null>(null);
  const [notificationsByPhone, setNotificationsByPhone] = useState<boolean | null>(null);
  const [editingField, setEditingField] = useState<EditingFieldProps>({isOpen: false, fieldName: "", title: "", value: ""});

  const { data } = useQuery<UserData>({
    queryKey: ["user"],
    queryFn: UserService.fetchUserData,
    staleTime: 0,
  });

  const mutation = useMutation({
    mutationFn: (details: UserDetailsRequest) => settingsService.updateUserDetails(details),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["user"] });
    },
    onError: () => {
      toast.error("Nie udało się zmienić danych!");
    }
  });

  useEffect(() => {
    if (data) {
      setDarkMode(data.darkMode);
      setNotificationsByEmail(data.notificationsByEmail);
      setNotificationsByPhone(data.notificationsByPhone);
    }
  }, [data]);

  const openEditPopup = (fieldName: string, title: string, value: string = "") => {
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
    if (!data) return;
    const payload = createPayload(fieldName || editingField.fieldName, value, data, darkMode, notificationsByEmail, notificationsByPhone);
    mutation.mutate(payload, {
      onSuccess: () => {
        toast.success("Udało się zmienić dane!");
      }
    });
  };

  useEffect(() => {
    if (!data || darkMode === null || notificationsByEmail === null || notificationsByPhone === null) return;
    const payload = createPayload(undefined, "", data, darkMode, notificationsByEmail, notificationsByPhone);
    mutation.mutate(payload);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [notificationsByEmail, notificationsByPhone, darkMode]);

  const accountOptions: AccountOption[] = [
    {
      text: "Zdjęcie",
      avatar: (
          <Image
              src="/avatar.png"
              width={45}
              height={45}
              alt="Avatar"
              className="rounded-full"
          />
      ),
      onClick: () => {toast.error("Not implemented")},
      fieldName: "avatar"
    },
    {
      text: "Nazwa użytkownika",
      value: data?.firstName
          ? `${data?.firstName} ${data?.lastName}`
          : "Użytkownik...",
      onClick: () => openEditPopup(
          "name",
          "Edytuj nazwę użytkownika",
          data?.firstName ? `${data?.firstName} ${data?.lastName}` : ""
      ),
      fieldName: "name"
    },
    {
      text: "E-mail",
      value: data?.email || "Email...",
      onClick: () => openEditPopup("email", "Edytuj adres email", data?.email || ""),
      fieldName: "email"
    },
    {
      text: "Numer telefonu",
      value: data?.phoneNumber || "Nie podano...",
      onClick: () => openEditPopup("phoneNumber", "Edytuj numer telefonu", data?.phoneNumber || ""),
      fieldName: "phoneNumber"
    },
  ];

  return (
      <div className="w-full p-8 min-h-screen xl:px-32 xl:py-20 font-semibold text-accent">
        <div>
          <h1 className="text-4xl">Ustawienia</h1>
        </div>

        <div className="flex flex-col sm:flex-row gap-y-2 sm:gap-x-6 mt-8">
          {navLinks.map((link) => (
              <div
                  key={link}
                  className="px-6 py-2 bg-neutral-200 rounded-lg hover:cursor-pointer hover:bg-neutral-300 transition-all ease-in-out duration-200"
                  onClick={() => scrollToSection(link)}
              >
                <p className="text-lg">{link}</p>
              </div>
          ))}
        </div>

        <AccountSection accountOptions={accountOptions} />
        <PreferencesSection darkMode={darkMode} setDarkMode={setDarkMode} />
        <NotificationsSection
            notificationsByEmail={notificationsByEmail}
            setNotificationsByEmail={setNotificationsByEmail}
            notificationsByPhone={notificationsByPhone}
            setNotificationsByPhone={setNotificationsByPhone}
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
      </div>
  );
}