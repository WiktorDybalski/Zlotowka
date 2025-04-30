import { Dream } from "@/services/DreamsService";
import GenericCard from "../dashboard/cards/generic/GenericCard";
import ThreeElementsCard from "../dashboard/cards/generic/ThreeElementsCard";
import TextNumberField from "../dashboard/cards/generic/TextNumberField";
import Link from "next/link";
import { ProgressBar } from "../general/ProgressBar";

interface DreamCardProps {
  dream: Dream;
}

const maxDescriptionLength = 100; // Maximum length of the description to display

export default function DreamCard({ dream }: DreamCardProps) {
  const truncatedDescription =
    dream.description.length > maxDescriptionLength
      ? dream.description.substring(0, 100) + "..."
      : dream.description;
  return (
    <figure style={{ width: "370px", height: "270px" }}>
      <GenericCard
        className={`pt-4 ${dream.completed ? "bg-lightAccent" : ""}`}
      >
        <Link href={`/dreams/${dream.planId}`}>
          <ThreeElementsCard
            top={
              <>
                <p className="text-3xl font-semibold">
                  <span className="mr-4">#</span>
                  <span>{dream.name}</span>
                </p>
                <p className="font-lato text-xl mt-3 mb-2">
                  <span>
                    {dream.actualAmount} {dream.currency.isoCode}
                  </span>
                  <span className="mx-1">/</span>
                  <span>
                    {dream.amount} {dream.currency.isoCode}
                  </span>
                </p>
                <ProgressBar progress={dream.actualAmount / dream.amount} />
                <p className="text-sm mt-6">{truncatedDescription}</p>
              </>
            }
            middle={<></>}
            bottom={
              <TextNumberField
                text={dream.canBeCompleted ? "Można wykonać!" : ""}
                number={dream.date}
              />
            }
          />
        </Link>
      </GenericCard>
    </figure>
  );
}
