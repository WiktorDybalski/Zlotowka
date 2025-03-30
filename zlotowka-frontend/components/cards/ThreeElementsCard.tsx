import GenericCard from "@/components/cards/GenericCard";
import {ReactNode} from "react";

interface ThreeElementsCardProps {
  top: ReactNode;
  middle: ReactNode;
  bottom: ReactNode;
}


export default function ThreeElementsCard({ top, middle, bottom }: ThreeElementsCardProps) {
  return (
      <GenericCard>
          <div className="w-full h-full px-7 py-5 flex flex-col justify-center font-semibold text-neutral-800">
            <div className="w-full text-xl">
              {top}
            </div>
            <div className="w-full text-4xl xl:text-3xl 2xl:text-4xl font-lato my-auto py-4">
              {middle}
            </div>
            <div className="w-full text-xl">
              {bottom}
            </div>
          </div>
      </GenericCard>
  )
}