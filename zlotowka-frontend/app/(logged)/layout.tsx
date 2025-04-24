import Navigation from "@/components/navigation/Navigation";
import { EnterWithAuth } from "@/components/providers/LoginProvider";
import { Toaster } from "react-hot-toast";

export default function HomeLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <EnterWithAuth>
      <div className="flex w-full flex-col font-semibold xl:flex-row">
        <Toaster position="top-center" reverseOrder={false} />
        <div className="w-full min-h-20 left-0 top-0 text-background xl:w-96 xl:h-screen xl:sticky">
          <Navigation />
        </div>
        <div className="w-full">{children}</div>
      </div>
    </EnterWithAuth>
  );
}
