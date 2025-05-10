import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import {ChartDreamsData} from "@/services/DreamsService";
import {UserData} from "@/services/UserService";
import {UserDetailsRequest} from "@/interfaces/settings/Settings";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getRoundedDomain(
    dreams: ChartDreamsData[],
    step: number = 100,
    filterType?: 'PLAN' | 'SUBPLAN'
): [number, number] {
  const filteredDreams = filterType
      ? dreams.filter(d => d.planType === filterType)
      : dreams;

  const dreamAmounts = filteredDreams?.map(d => d.requiredAmount * 1.05) ?? [];

  if (dreamAmounts.length === 0) return [0, step];

  const min = Math.min(...dreamAmounts);
  const max = Math.max(...dreamAmounts);
  const roundedMin = Math.floor(min / step) * step;
  const roundedMax = Math.ceil(max / step) * step;

  return [roundedMin, roundedMax];
}

export function createPayload(
    fieldName: string | undefined,
    value: string,
    data: UserData,
    darkMode: boolean,
    notificationsByEmail: boolean,
    notificationsByPhone: boolean
): UserDetailsRequest {

  let firstName = data.firstName;
  let lastName = data.lastName;

  if (fieldName === "name" && value.length !== 0) {
    const parts = value.trim().split(" ");
    firstName = parts[0];
    lastName = parts.slice(1).join(" ") || "";
  } else {
    if (fieldName === "firstName") firstName = value;
    if (fieldName === "lastName")  lastName  = value;
  }

  return {
    firstName,
    lastName,
    email:       fieldName === "email"       ? value : data.email,
    phoneNumber: fieldName === "phoneNumber" ? value : data.phoneNumber,
    darkMode: darkMode ? "true" : "false",
    notificationsByEmail: notificationsByEmail ? "true" : "false",
    notificationsByPhone: notificationsByPhone ? "true" : "false",
  };
}