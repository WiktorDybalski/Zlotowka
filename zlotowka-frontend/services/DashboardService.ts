import { API_HOST } from "@/lib/config";

const Dashboard = {
  getMainChartData: async (userId: number, startDate: string, endDate: string) => {
    try {
      const response = await fetch(`http://${API_HOST}/general-transactions/plot-data`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          userId: userId,
          startDate: startDate,
          endDate: endDate,
        }),
      });
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },
  getPieSideChartData: async (userId: number) => {
    try {
      const response = await fetch(
        `http://${API_HOST}/general-transactions/monthly-summary/${userId}`,
      );
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  },
};

export default Dashboard;
