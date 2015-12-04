//package example.binarylight;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceId;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceId;
import org.fourthline.cling.model.types.UDN;

public class BinaryLightServer implements Runnable {

//	JButton btn = new JButton("On");
	LocalDevice lightBulb;
	JLabel lbl = null;

	public static void main(String[] args) throws Exception {
	        // Start a user thread that runs the UPnP stack
	        Thread serverThread = new Thread(new BinaryLightServer());
	        serverThread.setDaemon(false);
	        serverThread.start();
	}

    public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            initUI();

            lightBulb = createDevice();
            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    lightBulb
            );
            ServiceId serviceId = new UDAServiceId("SwitchPower");
            Service switchPowerService = lightBulb.findService(serviceId);
//            switchPowerService.
            SubscriptionCallback callback = new SubscriptionCallback(switchPowerService, 600) {

                @Override
                public void established(GENASubscription sub) {
                    System.out.println("Established: " + sub.getSubscriptionId());
                }

                @Override
                protected void failed(GENASubscription subscription,
                                      UpnpResponse responseStatus,
                                      Exception exception,
                                      String defaultMsg) {
                    System.err.println(defaultMsg);
                }

                @Override
                public void ended(GENASubscription sub,
                                  CancelReason reason,
                                  UpnpResponse response) {
//                    assertNull(reason);
                }

                @Override
                public void eventReceived(GENASubscription sub) {

                    System.out.println("Event: " + sub.getCurrentSequence().getValue());

                    Map<String, StateVariableValue> values = sub.getCurrentValues();
                    StateVariableValue status = values.get("Status");

                    System.out.println("Status is: " + status.toString());

                    if (status.toString().equals("0")) {
                    	try {
							lbl.setIcon(new ImageIcon(ImageIO.read(new File("resources/lightoff.png"))));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    } else {
                    	try {
							lbl.setIcon(new ImageIcon(ImageIO.read(new File("resources/lighton.png"))));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }

                }

                @Override
                public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
                    System.out.println("Missed events: " + numberOfMissedEvents);
                }

                @Override
                protected void invalidMessage(RemoteGENASubscription sub,
                                              UnsupportedDataException ex) {
                    // Log/send an error report?
                }
            };

            upnpService.getControlPoint().execute(callback);


        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void initUI() {
    	JFrame frame = new JFrame("Light Bulb");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage img = null;
		try {
			img = ImageIO.read(new File("resources/lightoff.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ImageIcon icon = new ImageIcon(img);
        lbl = new JLabel(icon);

        JPanel panel = new JPanel();
        panel.add(lbl);
        frame.setContentPane(panel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setSize(300, 300);


    }

    // DOC: CREATEDEVICE
    LocalDevice createDevice()
            throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity =
                new DeviceIdentity(
                        UDN.uniqueSystemIdentifier("Demo Binary Light")
                );

        DeviceType type =
                new UDADeviceType("BinaryLight", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "Friendly Binary Light",
                        new ManufacturerDetails("ACME"),
                        new ModelDetails(
                                "BinLight2000",
                                "A demo light with on/off switch.",
                                "v1"
                        )
                );

        URL url = getClass().getResource("icon.png");
        System.out.println("url = " + url);
        Icon icon =
                new Icon(
                        "image/png", 48, 48, 8,
                        url
                );

        @SuppressWarnings("unchecked")
		LocalService<SwitchPower> switchPowerService =
                new AnnotationLocalServiceBinder().read(SwitchPower.class);


        switchPowerService.setManager(
                new DefaultServiceManager(switchPowerService, SwitchPower.class)
        );

        return new LocalDevice(identity, type, details, icon, switchPowerService);

        /* Several services can be bound to the same device:
        return new LocalDevice(
                identity, type, details, icon,
                new LocalService[] {switchPowerService, myOtherService}
        );
        */

    }
    // DOC: CREATEDEVICE
}
