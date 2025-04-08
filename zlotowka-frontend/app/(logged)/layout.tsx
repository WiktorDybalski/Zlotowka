import Navigation from "@/components/navigation/Navigation";
import {Toaster} from "react-hot-toast";

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <div className="flex w-full flex-col font-semibold xl:flex-row">
      <Toaster
          position="top-center"
          reverseOrder={false}
      />
      <div className="w-full min-h-20 left-0 top-0 text-neutral-100 xl:w-96 xl:h-screen xl:sticky">
        <Navigation />
      </div>
      <div className="w-full">{children}</div>
    </div>
  );
}
