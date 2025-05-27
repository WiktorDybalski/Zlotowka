import {ReactNode} from "react";

export interface AccountFieldProps {
  text: string;
  value?: string;
  avatar?: ReactNode;
  onClick?: () => void;
}

export interface AccountFieldProps {
  text: string;
  value?: string;
  avatar?: ReactNode;
  onClick?: () => void;
}

export interface ToggleOptionProps {
  label: string;
  enabled: boolean;
  onToggle: () => void;
}

export interface AccountOption {
  text: string;
  value?: string;
  avatar?: ReactNode;
  onClick: () => void;
  fieldName: string;
}

export interface AccountSectionProps {
  accountOptions: AccountOption[];
}

export interface PreferencesSectionProps {
  darkMode: boolean;
  setDarkMode: (value: boolean) => void;
}

export interface NotificationsSectionProps {
  notificationsByEmail: boolean;
  setNotificationsByEmail: (value: boolean) => void;
  notificationsByPhone: boolean;
  setNotificationsByPhone: (value: boolean) => void;
}

export interface EditFieldPopupProps {
  onCloseAction: () => void;
  title: string;
  fieldName: string;
  initialValue?: string;
  onSave: (value: string, fieldName: string) => void;
}

export interface UserDetailsRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  darkMode: "true" | "false";
  notificationsByEmail: "true" | "false";
  notificationsByPhone: "true" | "false";
}

export interface EditingFieldProps {
  isOpen: boolean;
  fieldName: string;
  title: string;
  value: string;
}
