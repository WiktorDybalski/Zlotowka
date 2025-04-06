const API_HOST = 'http://localhost:3000/api';

const Dashboard = {
  getMainChartData: async () => {
    try {
      const response = await fetch(`${API_HOST}/generalTransactionService/mainChartData`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },
  getPieSideChartData: async () => {
    try {
      const response = await fetch(`${API_HOST}/generalTransactionService/pieSideData`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  }
}

export default Dashboard