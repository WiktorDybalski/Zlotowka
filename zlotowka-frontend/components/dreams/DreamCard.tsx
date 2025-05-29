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

  let completedMessage = "";
  if (dream.canBeCompleted) completedMessage = "Można wykonać!";
  if (dream.completed) completedMessage = "Zrealizowano!";

  return (
    <figure>
      <GenericCard
        className={`pt-1 ${dream.completed ? "bg-lightAccent" : ""}`}
      >
        <Link href={`/dreams/${dream.planId}`}>
          <ThreeElementsCard
            top={
              <>
                <div className="flex items-center gap-x-2">
                  <span className="mr-4">#</span>
                  <span>{dream.name}</span>
                  {dream.completed && (
                    <span
                      className="material-symbols "
                      style={{ fontSize: "1.7rem" }}
                    >
                      done
                    </span>
                  )}
                </div>

                <p className="font-lato text-xl mt-3 mb-2">
                  <span>
                    {Math.min(dream.actualAmount, dream.amount).toLocaleString(
                      undefined,
                      {
                        minimumFractionDigits: 0,
                        maximumFractionDigits: 2,
                      }
                    )}{" "}
                    {dream.currency.isoCode}
                  </span>
                  <span className="mx-1">/</span>
                  <span>
                    {dream.amount} {dream.currency.isoCode}
                  </span>
                </p>
                <ProgressBar progress={dream.actualAmount / dream.amount} />
                <p className="text-sm mt-6 overflow-hidden whitespace-nowrap text-ellipsis">
                  {truncatedDescription}
                </p>
              </>
            }
            middle={<></>}
            bottom={
              <TextNumberField text={completedMessage} number={dream.date} />
            }
          />
        </Link>
      </GenericCard>
    </figure>
  );
}
