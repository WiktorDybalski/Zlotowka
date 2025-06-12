"use client";

import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";
import { Dream, useDreamsService } from "@/services/DreamsService";
import { useQueryWithToast } from "@/lib/data-grabbers";

type DreamContextType = {
  pickedDream: number | null;
  handlePickDream: (id: number) => void;
};

const DreamContext = createContext<DreamContextType | undefined>(undefined);

export const DreamProvider = ({ children }: { children: ReactNode }) => {
  const [pickedDream, setPickedDream] = useState<number | null>(null);
  const DreamService = useDreamsService();

  const { data: dreams } = useQueryWithToast<Dream[]>({
    queryKey: ["dreams", "getAllDreams"],
    queryFn: DreamService.getAllDreams,
    enabled: pickedDream === null && typeof window !== "undefined",
  });

  useEffect(() => {
    const storedId = localStorage.getItem("pickedDream");
    if (storedId) {
      setPickedDream(Number(storedId));
    } else if (dreams && dreams.length > 0) {
      setPickedDream(dreams[0].planId);
    }
  }, [dreams]);

  useEffect(() => {
    if (pickedDream !== null) {
      localStorage.setItem("pickedDream", pickedDream.toString());
    }
  }, [pickedDream]);

  const handlePickDream = (id: number) => {
    setPickedDream(id);
  };

  return (
    <DreamContext.Provider value={{ pickedDream, handlePickDream }}>
      {children}
    </DreamContext.Provider>
  );
};

export const useDreamContext = (): DreamContextType => {
  const context = useContext(DreamContext);
  if (!context) {
    throw new Error("useDreamContext must be used within a DreamProvider");
  }
  return context;
};
