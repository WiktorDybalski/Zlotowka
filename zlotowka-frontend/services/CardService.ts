import { API_HOST } from "@/lib/config";

const CardService = {
  getCurrentBalance: async (userId: number) => {
    try {
      const response = await fetch(
        `https://${API_HOST}/general-transactions/current-balance/${userId}`,
      );
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },

  getMonthEstimatedBalance: async (userId: number) => {
    try {
      console.log(`https://${API_HOST}/general-transactions/estimated-balance/${userId}`);
      const response = await fetch(
        `https://${API_HOST}/general-transactions/estimated-balance/${userId}`,
      );
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },

  getNextTransaction: async (userId: number, isIncome: boolean) => {
    try {
      const response = await fetch(
        `https://${API_HOST}/general-transactions/next-transaction/${userId}?isIncome=${isIncome}`,
      );
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },
};

export default CardService;
