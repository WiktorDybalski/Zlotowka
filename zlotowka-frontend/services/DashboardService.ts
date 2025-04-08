import {API_HOST} from "@/lib/config";

const Dashboard = {
  getMainChartData: async (userId: number, startDate: Date, endDate: Date) => {
    try {
      const response = await fetch(`${API_HOST}/generaltransactions/plotData`, {
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
      const response = await fetch(`${API_HOST}/generaltransactions/revenuesAndExpenses/${userId}`);
      return await response.json();
    } catch (error) {
      console.log(error);
    }
  }
}

export default Dashboard