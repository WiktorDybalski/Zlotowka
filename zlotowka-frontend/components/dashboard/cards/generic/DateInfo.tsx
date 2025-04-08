import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import {DateInfoProps} from "@/interfaces/dashboard/cards/DateInfoProps";

export default function DateInfo({ text, date }: DateInfoProps) {
  return (
      <div className="w-full flex justify-between">
        <CardText text={text} />
        <CardNumber text={date} />
      </div>
  );
}