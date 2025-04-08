import {API_HOST} from "@/lib/config";

const CardService = {
  getCurrentBalance: async (userId: number) => {
    try {
      const response = await fetch(`${API_HOST}/generaltransactions/currentBalance/${userId}`);
      console.log(response);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },

  getMonthEstimatedBalance: async (userId: number) => {
    try {
      const response = await fetch(`${API_HOST}/generaltransactions/estimatedBalance/${userId}`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },

  getNextTransaction: async (userId: number) => {
    try {
      const response = await fetch(`${API_HOST}/generaltransactions/nextTransaction/${userId}`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  }
}

export default CardService