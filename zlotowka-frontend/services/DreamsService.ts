import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, {
  getAuthHeader,
  sendToBackendWithoutReturningJson,
} from "@/lib/sendToBackend";
import { Currency } from "./CurrencyController";

export interface Dream {
  planId: number;
  // userId: number;
  name: string;
  description: string;
  amount: number;
  date: string; // ISO date
  currency: Currency;
  completed: boolean;
  actualAmount: number;
  canBeCompleted: boolean;
  subplansCompleted: number;
}

export interface SubDream {
  planId: number;
  subplanId: number;
  name: string;
  description: string;
  amount: number;
  currency: Currency;
  completed: boolean;
  actualAmount: number;
  canBeCompleted: boolean;
  date: string; // ISO date
}

export interface NewSubDreamReq {
  planId: number;
  // userId: number;
  name: string;
  description: string;
  amount: number;
  currencyId: number;
}

export interface NewDreamReq {
  // userId: number;
  name: string;
  description: string;
  amount: number;
  currencyId: number;
}

export interface DreamWithSubplans extends Dream {
  subplans: Array<SubDream>;
}

export interface ChartDreamsData {
  id: number;
  name: string;
  requiredAmount: number;
  planType: "PLAN" | "SUBPLAN";
}

export function useDreamsService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getChartDreamsData(): Promise<Array<ChartDreamsData>> {
    return await sendToBackend(
      `general-plans/chart-data/${userId}`,
      withAuthHeader,
      "Nie udało się pobrać danych do wykresu dla marzeń"
    );
  }

  async function getAllDreams(): Promise<Array<Dream>> {
    return await sendToBackend(
      `plan/all/${userId}`,
      withAuthHeader,
      "Nie udało się pobrać marzeń"
    );
  }

  async function getDream(dreamId: number): Promise<DreamWithSubplans> {
    const dream: Dream = await sendToBackend(
      `plan/${dreamId}`,
      withAuthHeader,
      "Failed to fetch dream"
    );
    const subplans: Array<SubDream> = await sendToBackend(
      `subplan/all/${dream.planId}`,
      withAuthHeader,
      "Nie udało się pobrać subplanów"
    );
    return {
      ...dream,
      subplans: subplans,
    };
  }

  async function addDream(dream: NewDreamReq): Promise<Dream> {
    return await sendToBackend(
      `plan`,
      {
        ...withAuthHeader,
        method: "POST",
        body: JSON.stringify({ ...dream, userId: userId }),
      },
      "Nie udało się dodać marzenia"
    );
  }

  async function completeDream(dreamId: number) {
    return await sendToBackend(
      `plan/complete/${dreamId}`,
      {
        ...withAuthHeader,
        method: "POST",
      },
      "Nie udało się oznaczyć marzenia jako zrealizowane"
    );
  }

  async function deleteDream(dreamId: number) {
    return await sendToBackendWithoutReturningJson(
      `plan/${dreamId}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Nie udało się usunąć marzenia"
    );
  }

  async function addSubDream(subDream: NewSubDreamReq) {
    return await sendToBackend(
      `subplan`,
      {
        ...withAuthHeader,
        method: "POST",
        body: JSON.stringify({ ...subDream, userId: userId }),
      },
      "Nie udało się dodać subplanu"
    );
  }

  async function completeSubDream(subDreamId: number) {
    return await sendToBackend(
      `subplan/complete/${subDreamId}`,
      {
        ...withAuthHeader,
        method: "POST",
      },
      "Nie udało się oznaczyć subplanu jako zrealizowany"
    );
  }

  async function deleteSubDream(subDreamId: number) {
    return await sendToBackendWithoutReturningJson(
      `subplan/${subDreamId}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Nie udało się usunąć subplanu"
    );
  }

  return {
    getAllDreams,
    getChartDreamsData,
    getDream,
    completeDream,
    deleteDream,
    addSubDream,
    completeSubDream,
    deleteSubDream,
    addDream,
  };
}
