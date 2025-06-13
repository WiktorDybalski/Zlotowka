import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import { ChartDreamsData } from "@/services/DreamsService";
import { UserData } from "@/services/UserService";
import { UserDetailsRequest } from "@/interfaces/settings/Settings";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getRoundedDomain(
  dreams: ChartDreamsData[],
  step: number = 100,
  filterType?: "PLAN" | "SUBPLAN",
): [number, number] {
  const filteredDreams = filterType
    ? dreams.filter((d) => d.planType === filterType)
    : dreams;

  const dreamAmounts =
    filteredDreams?.map((d) => d.requiredAmount * 1.05) ?? [];

  if (dreamAmounts.length === 0) return [0, step];

  const min = Math.min(...dreamAmounts);
  const max = Math.max(...dreamAmounts);
  const roundedMin = Math.floor(min / step) * step;
  const roundedMax = Math.ceil(max / step) * step;

  return [roundedMin, roundedMax];
}

export function validateSettings(
  value: string,
  fieldName?: string,
): string | null {
  if (!fieldName) return null;

  if (fieldName === "phoneNumber") {
    if (!value || typeof value !== "string" || !value.trim()) {
      return;
    }

    let phoneNumber = value.replaceAll("[\\s-]+", "").trim();

    if (!phoneNumber.startsWith("+")) {
      phoneNumber = "+48" + phoneNumber; // Poland by default
    }

    if (!/^\+?[1-9]\d{1,14}$/.test(phoneNumber)) { // E.164 standard
      return "Numer telefonu musi być w formacie międzynarodowym, zaczynając od '+' i używając tylko cyfr (np. +48 123 456 789).";
    }
  }

  if (fieldName === "name") {
    const parts = value.trim().split(" ");
    if (parts.length < 2 || parts.some((part) => part.length < 3)) {
      return "Imię i nazwisko powinno zawierać spację i mieć co najmniej po trzy litery!";
    }
  }

  if (fieldName === "email") {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(value)) {
      return "Nieprawidłowy adres e-mail!";
    }
  }

  return null;
}

export function createPayload(
  fieldName: string | undefined,
  value: string,
  data: UserData,
  darkMode: boolean,
  notificationsByEmail: boolean,
  notificationsByPhone: boolean,
): UserDetailsRequest {
  let firstName = data.firstName;
  let lastName = data.lastName;

  if (fieldName === "name" && value.length !== 0) {
    const parts = value.trim().split(" ");
    firstName = parts[0];
    lastName = parts.slice(1).join(" ") || "";
  } else {
    if (fieldName === "firstName") firstName = value;
    if (fieldName === "lastName") lastName = value;
  }

  return {
    firstName,
    lastName,
    email: fieldName === "email" ? value : data.email,
    phoneNumber: fieldName === "phoneNumber" ? value : data.phoneNumber,
    darkMode: darkMode ? "true" : "false",
    notificationsByEmail: notificationsByEmail ? "true" : "false",
    notificationsByPhone: notificationsByPhone ? "true" : "false",
  };
}
