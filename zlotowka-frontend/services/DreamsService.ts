import {useAuth} from "@/components/providers/AuthProvider";
import sendToBackend, {getAuthHeader, sendToBackendWithoutReturningJson,} from "@/lib/sendToBackend";
import {Currency} from "./CurrencyController";

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

export function useDreamsService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getAllDreamsWithSubplans(): Promise<Array<DreamWithSubplans>> {
    const dreams: Array<Dream> = await sendToBackend(
        `plan/all/${userId}`,
        withAuthHeader,
        "Failed to fetch dreams"
    );

    return await Promise.all(
        dreams.map(async (dream) => {
          const subplans: Array<SubDream> = await sendToBackend(
              `subplan/all/${dream.planId}`,
              withAuthHeader,
              `Failed to fetch subplans for dream ${dream.planId}`
          );

          return {
            ...dream,
            subplans,
          };
        })
    );
  }

  async function getAllDreams(): Promise<Array<Dream>> {
    return await sendToBackend(
      `plan/all/${userId}`,
      withAuthHeader,
      "Failed to fetch dreams"
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
      "Failed to fetch subplans"
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
      "Failed to add dream"
    );
  }

  async function completeDream(dreamId: number) {
    return await sendToBackend(
      `plan/complete/${dreamId}`,
      {
        ...withAuthHeader,
        method: "POST",
      },
      "Failed to complete dream"
    );
  }

  async function deleteDream(dreamId: number) {
    return await sendToBackendWithoutReturningJson(
      `plan/${dreamId}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Failed to delete dream"
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
      "Failed to add subdream"
    );
  }

  async function completeSubDream(subDreamId: number) {
    return await sendToBackend(
      `subplan/complete/${subDreamId}`,
      {
        ...withAuthHeader,
        method: "POST",
      },
      "Failed to complete subdream"
    );
  }

  async function deleteSubDream(subDreamId: number) {
    return await sendToBackendWithoutReturningJson(
      `subplan/${subDreamId}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Failed to delete subdream"
    );
  }

  return {
    getAllDreams,
    getAllDreamsWithSubplans,
    getDream,
    completeDream,
    deleteDream,
    addSubDream,
    completeSubDream,
    deleteSubDream,
    addDream,
  };
}
