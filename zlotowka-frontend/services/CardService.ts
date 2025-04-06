const API_HOST = 'http://localhost:3000/api';

const CardService = {
  getCurrentBalance: async (userId: number) => {
    try {
      const response = await fetch(`${API_HOST}/generalTransactionService/currentBalance?userId=${userId}`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },

  getMonthEstimatedBalance: async (userId: number) => {
    try {
      const response = await fetch(`${API_HOST}/generalTransactionService/estimatedMonthBalance?userId=${userId}`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },

  getNextTransaction: async (userId: number) => {
    try {
      const response = await fetch(`${API_HOST}/generalTransactionService/nextTransaction?userId=${userId}`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  }
}

export default CardService