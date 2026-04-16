package desperados.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

import desperados.service.FileService;
import desperados.util.PropertiesHandler;
import desperados.MainGUI;
import desperados.dvd.waypoints.WaypointParser;
import desperados.dvd.waypoints.WaypointRoute;
import desperados.dvm.DvmReader;
import desperados.exception.ServiceException;
import desperados.scb.ScbParser;

public class EditorWindow {
	//Variables
	public static String gameDir;
	public static String exeName;

	private final static String appName = "Desperados Mission Editor";
	private final static String appVersion = "v1.46, credits to herbert3000";

	public EditorWindow(MainGUI main) {
		gameDir = PropertiesHandler.getProperty("gameDir");
		exeName = PropertiesHandler.getProperty("exeName");
		FileService.setMain(main);
		//if (gameDir != null) {
			//FileService.setGameDir(gameDir);
		//}
		dvdLoaded = false;
		historyStack = new ArrayList<String[]>();
		historyCaret = new ArrayList<Integer>();
		historyScroll = new ArrayList<Integer>();
		historyActiveItem = new ArrayList<Integer>();
		historyIndex = -1;
		initComboItems();
	}

	private enum ScriptItems {
		ELEM("ELEM - Elements"),
		WAYS("WAYS - Waypoints"),
		SCRP("SCRP - Locations"),
		BUIL("BUIL - Buildings/Doors"),
		SCB(".SCB - Mission Script"),
		COORDS("Coordinate Parser");
		
		private String str;
		ScriptItems(String str) { this.str = str; }
		public String getString() { return str; }
	};

	private Image backgroundImage;
	int imageWidth;
	int imageHeight;
	
	private DropTarget dropTarget;
	private Clipboard clipboard;
	
	private boolean dvdLoaded;
	
	private Shell shell;
	private Canvas canvas;
	
	private boolean drawElements;
	private boolean drawObstacles;
	private boolean drawWaypoints;
	private boolean drawAI;
	private boolean drawLocations;
	private boolean drawDoors;
	private boolean drawMaterials;
	private boolean drawCoords;
	private boolean drawAnimations;
	private boolean drawIdentifier;
	private boolean isRestoringElementInfo;
	private Text textElementId;
	private Text textElementX;
	private Text textElementY;
	private Text textElementSprite;
	private Text textElementDirection;
	private Text textElementDvf;
	private Text textElementCharacter;
	
	private StyledText text;
	private StyledText textConsole;
	private StyledText textCoords;
	private Label spriteLabel;

	//Resultados de búsqueda actuales (índices de líneas que coinciden)
	private Text searchText;
	private java.util.List<Integer> searchMatches = new java.util.ArrayList<>();
	private int currentMatchIndex = -1;
	private Label searchCount;

	private String[] originalComboTexts;
	private Combo combo;
	private desperados.dvd.elements.Element currentElement;
	private Button prevElementButton;
	private Button nextElementButton;
	private ScrolledComposite scrolledCanvas;
	
	private ArrayList<String[]> historyStack;
	private ArrayList<Integer> historyCaret;
	private ArrayList<Integer> historyScroll;
	private ArrayList<Integer> historyActiveItem;
	private int historyIndex;
	private static final int MAX_HISTORY = 1000000;
	private Button undoButton;
	private Button redoButton;
	private boolean isRestoring = false;
	private boolean isFirstChange = true;
	private boolean isUpdatingFromPanel = false;

	private String[] lastSavedTexts;

	private java.util.LinkedHashSet<String> pendingLogicalChanges = new java.util.LinkedHashSet<>();

	//Valor inicial al entrar en edición de un campo del panel rojo
	private java.util.HashMap<Text, String> fieldEditStartValues = new java.util.HashMap<>();
	
	private int activeComboItem;
	private String[] comboItems;
	private String[] comboTexts;
	private int[] textPositions;
	private int[] unsavedChanges;

	private static class ParsedWayCommand {
		private final String name;
		private final String args;

		private ParsedWayCommand(String name, String args) {
			this.name = name;
			this.args = args;
		}
	}

	private static class ScbDiffEntry {
		int lineNumber;
		String text;
		String className;
		String functionName;

		ScbDiffEntry(int lineNumber, String text, String className, String functionName) {
			this.lineNumber = lineNumber;
			this.text = text;
			this.className = className;
			this.functionName = functionName;
		}
	}

	//Drag and drop de elementos directamente desde el mapa
	private boolean isDraggingElement = false;
	private desperados.dvd.elements.Element draggedElement = null;
	private int dragOffsetX;
	private int dragOffsetY;

	private boolean isCloneDrag = false;
	private desperados.dvd.elements.Element cloneElement = null;

	//Mover elementos con el teclado
	private boolean isKeyboardMoving = false;

	//Para ejecutar el juego directamente desde el editor (se asume que el usuario tiene el juego en Steam)
	private String steamAppId = "260730";

	//Animación de elementos, para detener al presionar back para volver a la ventana anterior
	private boolean animationRunning = false;

	//Aquí empiezan los métodos
	private void initComboItems() {
		activeComboItem = 0;
		
		comboItems = new String[]{
			ScriptItems.ELEM.getString(),
			ScriptItems.WAYS.getString(),
			ScriptItems.SCRP.getString(),
			ScriptItems.BUIL.getString(),
			ScriptItems.SCB.getString(),
			ScriptItems.COORDS.getString()
		};
		
		comboTexts = new String[comboItems.length];
		originalComboTexts = new String[comboItems.length];
		lastSavedTexts = new String[comboItems.length];
		unsavedChanges = new int[comboItems.length];

		for (int i = 0; i < comboTexts.length; i++) {
			comboTexts[i] = "TODO";
			originalComboTexts[i] = "TODO";
			lastSavedTexts[i] = "TODO";
			unsavedChanges[i] = 0;
		}
		
		textPositions = new int[comboItems.length];
	}

	public void run() {
		Display display = Display.getDefault();
		if (dvdLoaded) {
			createContents(display);
		} else {
			createContentsEmpty(display);
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	public void loadDvdData(String path) {
		
		backgroundImage = DvmReader.getDvmImage(path);
		if (backgroundImage != null) {
			imageWidth = backgroundImage.getImageData().width;
			imageHeight = backgroundImage.getImageData().height;
		}
		
		loadElementText();
		loadWaypointText();
		loadScriptText();
		loadLocationsText();
		loadBuildingsText();
		
		comboTexts[ScriptItems.COORDS.ordinal()] = "Insert coordinates (x,y) here.";
		originalComboTexts[ScriptItems.COORDS.ordinal()] = "Insert coordinates (x,y) here.";
		lastSavedTexts[ScriptItems.COORDS.ordinal()] = "Insert coordinates (x,y) here.";
		
		dvdLoaded = true;
		
		shell.dispose();
		run();
	}

	private void loadElementText() {
		String text = FileService.getElementText();
		int idx = ScriptItems.ELEM.ordinal();
		comboTexts[idx] = text;
		originalComboTexts[idx] = text;
		lastSavedTexts[idx] = text;
	}

	private void loadScriptText() {
		String text = FileService.readScbFile();
		int idx = ScriptItems.SCB.ordinal();
		comboTexts[idx] = text;
		originalComboTexts[idx] = text;
		lastSavedTexts[idx] = text;
	}

	private void loadLocationsText() {
		String text = FileService.getLocationText();
		int idx = ScriptItems.SCRP.ordinal();
		comboTexts[idx] = text;
		originalComboTexts[idx] = text;
		lastSavedTexts[idx] = text;
	}

	private void loadBuildingsText() {
		String text = FileService.getBuildingsText();
		int idx = ScriptItems.BUIL.ordinal();
		comboTexts[idx] = text;
		originalComboTexts[idx] = text;
		lastSavedTexts[idx] = text;
	}

	private void loadWaypointText() {
		List<WaypointRoute> routes = FileService.getWaypointRoutes();
		if (routes != null) {
			String str = "";
			for (WaypointRoute r : routes) {
				str += r.toString() + "\n";
			}
			int idx = ScriptItems.WAYS.ordinal();
			comboTexts[idx] = str;
			originalComboTexts[idx] = str;
			lastSavedTexts[idx] = str;
		}
	}

	private void createContentsEmpty(Display display) {
		shell = new Shell();
		shell.setText(appName + " " + appVersion);
		
		dropTarget = new DropTarget(shell, DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		
		DropTargetAdapter dropTargetAdapter = new DropTargetAdapter() {
			public void drop(DropTargetEvent e) {
				String fileList[] = null;
				FileTransfer ft = FileTransfer.getInstance();
			    
				if (ft.isSupportedType(e.currentDataType)) {
			    	fileList = (String[])e.data;
			    }
				if (fileList.length != 0) {
					for (String filename : fileList) {
						if (filename.endsWith(".dvd") || filename.endsWith(".json")) {
							Display.getDefault().asyncExec(new Runnable() {
						        public void run() {
						        	try {
										FileService.readFile(filename);
									} catch (ServiceException e) {
										e.printStackTrace();
									}
						        }
						    });
							return;
						}
					}
			    }
			}
		};
		dropTarget.addDropListener(dropTargetAdapter);
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		
		Link link = new Link(shell, SWT.NONE);
		link.setText("Commandos HQ Forums - <a>Desperados Research</a>");
		link.addSelectionListener(new SelectionAdapter()  {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        Program.launch("https://forums.revora.net/topic/106804-desperados-modding-research/");
		    }
		});
		
		Link link2 = new Link(shell, SWT.NONE);
		link2.setText("Desperados Discord Server - <a>Modding Channel</a>");
		link2.addSelectionListener(new SelectionAdapter()  {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		        Program.launch("https://discord.com/channels/437970717850730499/679682654555471873");
		    }
		});
		
		//Label label = new Label(shell, SWT.NONE);
		//label.setText("Drag and drop a .dvd file onto this window.");
		
		Button button = new Button(shell, SWT.NONE);
		String buttonText = "Set Desperados Directory";
		if (gameDir != null) {
			buttonText += " (already set)";
		}
		button.setText(buttonText);
		button.addListener(SWT.Selection, new Listener() {
		    @Override
		    public void handleEvent(Event event) {
		    	DirectoryDialog dlg = new DirectoryDialog(shell);
				dlg.setFilterPath("C:/");
				dlg.setMessage("Select your Desperados directory");
				String dir = dlg.open();
				if (dir != null) {
					File d = new File(dir + "\\Game");
					if (d.exists()) dir += "\\Game";
					
					gameDir = dir;
					//FileService.setGameDir(dir);
					
					String exeNames[] = {"desperados.exe", "game.exe", "Game_demo.exe"};
					for (int i = 0; i < exeNames.length; i++) {
						File exe = new File(gameDir + "\\" + exeNames[i]);
						if (exe.exists()) {
							exeName = exeNames[i];
							PropertiesHandler.setProperty("exeName", exeNames[i]);
							break;
						} 
					}
					
					PropertiesHandler.setProperty("gameDir", dir);
					PropertiesHandler.storeProperties();
				}
		    }
		});
		
		Label lbl = new Label(shell, SWT.NONE);
		lbl.setText("Drag and drop a .dvd file or Enter level number (1 to 25) and press enter:");
		
		Text text = new Text(shell, SWT.BORDER);
		text.setFocus();
		
		text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR || e.keyCode == SWT.LF) {
					
					if (gameDir == null) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
						messageBox.setText("Warning");
						messageBox.setMessage("Set Desperados Directory first!");
						messageBox.open();
						return;
					}
					
					String level = text.getText();
					text.setText("");
					
					if (level.length() == 1) {
						level = "0" + level;
					}
					
					String filename = gameDir + "\\data\\levels\\level_" + level + ".dvd";
					System.out.println(filename);
					
					Display.getDefault().asyncExec(new Runnable() {
				        public void run() {
				        	try {
								FileService.readFile(filename);
							} catch (ServiceException e) {
								e.printStackTrace();
							}
				        }
				    });
				
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
	}

	private void createContents(Display display) {
		shell = new Shell();
		shell.setText(appName + " " + appVersion);
		
		clipboard = new Clipboard(display);

		display.addFilter(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event e) {
				// Solo si esta ventana/app está activa
				if (shell == null || shell.isDisposed()) {
					return;
				}

				Shell activeShell = display.getActiveShell();
				if (activeShell != shell) {
					return;
				}

				boolean ctrlPressed = (e.stateMask & SWT.CTRL) != 0;

				if (ctrlPressed && (e.keyCode == 'z' || e.keyCode == 'Z')) {
					undo();
					e.type = SWT.None;
					e.doit = false;
					return;
				}

				if (ctrlPressed && (e.keyCode == 'y' || e.keyCode == 'Y')) {
					redo();
					e.type = SWT.None;
					e.doit = false;
					return;
				}
			}
		});
		
		//Layout para el shell con el SashForm
		GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		shell.setLayout(shellLayout);
		
		// Usar SashForm en lugar de GridLayout para permitir redimensionamiento
		SashForm mainSash = new SashForm(shell, SWT.HORIZONTAL);
		mainSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ScrolledComposite sc = new ScrolledComposite(mainSash, SWT.H_SCROLL | SWT.V_SCROLL);
		
		// Thread updateThread = new Thread() {
	    //     public void run() {
	    //         while (true) {
	    //             display.syncExec(new Runnable() {
	    //                 @Override
	    //                 public void run() {
	    //                 	if ((drawAnimations || drawElements) && !canvas.isDisposed()) {
	    //                 		canvas.redraw();
	    //                 	}
	    //                 }
	    //             });
	    //             try {
	    //                 Thread.sleep(200);
	    //             } catch (InterruptedException e) {
	    //                 e.printStackTrace();
	    //             }
	    //         }
	    //     }
	    // };
	    // updateThread.setDaemon(true);
	    // updateThread.start();
		
		canvas = new Canvas(sc, SWT.DOUBLE_BUFFERED);
		startAnimationLoop();
		DropTarget canvasDropTarget = new DropTarget(canvas, DND.DROP_COPY | DND.DROP_MOVE);
		canvasDropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });

		canvasDropTarget.addDropListener(new DropTargetAdapter() {
			@Override
			public void dragEnter(DropTargetEvent event) {
				if (event.currentDataType == null) return;

				if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
					event.detail = DND.DROP_COPY; // 🔥 SIN ESTO → 🚫
				}
			}

			@Override
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}

			@Override
			public void drop(DropTargetEvent event) {
				if (!TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
					return;
				}

				String data = (String) event.data;

				if (data != null) {
					String sourceId = data.toString();

					Point p = canvas.toControl(event.x, event.y);

					cloneElementAt(sourceId, p.x, p.y);
				}
			}
		});
		
		canvas.addListener(SWT.MouseUp, new Listener() {
			public void handleEvent(Event e) {
				// Terminar drag si estaba activo
				if (isDraggingElement) {
					isDraggingElement = false;

					// Mover → regenerar JSON
					regenerateJSON();

					// Sincronizar con FileService
					FileService.setElementText(text.getText());

					text.notifyListeners(SWT.Modify, new Event());

					// Clone ya fue insertado en MouseDown
					displayElementInfo(draggedElement);

					isCloneDrag = false;
					cloneElement = null;

					return;
				}

				String coordText = (e.x) + "," + (e.y);
				textCoords.setText(coordText);
				copyToClipboard(coordText);

				boolean clickedElement = false;

				// Detectar si hizo click en algún elemento
				if (drawElements || drawAnimations) {
					clickedElement = detectClickedElement(e.x, e.y);
				}

				// 👉 CLICK DERECHO en vacío
				if (e.button == 3 && !clickedElement) {
					openMapContextMenu(e.x, e.y);
				}
			}
		});
		
		canvas.addListener(SWT.MouseDown, e -> {
			if (!(drawElements || drawAnimations)) return;

			boolean clicked = detectClickedElement(e.x, e.y);

			if (clicked && currentElement != null) {
				boolean ctrlPressed = (e.stateMask & SWT.CTRL) != 0;

				if (ctrlPressed) {
					// 🔥 Modo clon
					isCloneDrag = true;

					int nextId = findNextElementId(comboTexts[ScriptItems.ELEM.ordinal()]);
					String newId = "Element_" + nextId;

					cloneElement = cloneElementObject(currentElement, newId);

					if (cloneElement == null) return;

					draggedElement = cloneElement;

				} else {
					// Modo normal: mover elemento existente
					isCloneDrag = false;
					draggedElement = currentElement;
				}

				isDraggingElement = true;

				int originX = scrolledCanvas.getOrigin().x;
				int originY = scrolledCanvas.getOrigin().y;

				int mouseX = e.x + originX;
				int mouseY = e.y + originY;

				dragOffsetX = mouseX - draggedElement.getX();
				dragOffsetY = mouseY - draggedElement.getY();
			}
		});
		
		canvas.addListener(SWT.MouseMove, e -> {
			if (!isDraggingElement || draggedElement == null) return;

			int originX = scrolledCanvas.getOrigin().x;
			int originY = scrolledCanvas.getOrigin().y;

			int mouseX = e.x + originX;
			int mouseY = e.y + originY;

			int newX = Math.max(0, Math.min(32767, mouseX - dragOffsetX));
			int newY = Math.max(0, Math.min(32767, mouseY - dragOffsetY));

			draggedElement.setX((short)newX);
			draggedElement.setY((short)newY);

			canvas.redraw();
		});
		
		//Atajos de teclado para mover elementos con las flechas
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (currentElement == null) return;

				int step = 1;

				if ((e.stateMask & SWT.SHIFT) != 0) step = 10;
				if ((e.stateMask & SWT.CTRL) != 0) step = 50;

				int dx = 0;
				int dy = 0;

				switch (e.keyCode) {
					case SWT.ARROW_LEFT:  dx = -step; break;
					case SWT.ARROW_RIGHT: dx = step;  break;
					case SWT.ARROW_UP:    dy = -step; break;
					case SWT.ARROW_DOWN:  dy = step;  break;
					default: return;
				}

				moveCurrentElement(dx, dy);
			}
		});
		canvas.addListener(SWT.MouseDown, e -> {
			canvas.setFocus();
		});
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				if (!isKeyboardMoving) return;

				isKeyboardMoving = false;
				displayElementInfo(currentElement);

				// Ahora sí: commit real
				regenerateJSON();
				text.notifyListeners(SWT.Modify, new Event());
			}
		});

		canvas.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.F1) {
					Program.launch(gameDir + "\\" + exeName, gameDir);
				}
			}
		});
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				
				if (backgroundImage != null) {
					e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					e.gc.fillRectangle(0, 0, imageWidth, imageHeight);
					e.gc.drawImage(backgroundImage, 0, 0, imageWidth, imageHeight, 0, 0, imageWidth, imageHeight);
				}
				
				if (drawAnimations) {
					FileService.drawAnimations(display, e);
				}
				if (drawElements) {
					FileService.drawElements(display, e);
				}
				if (drawObstacles) {
					FileService.drawObstacles(display, e);
				}
				if (drawWaypoints) {
					FileService.drawWaypoints(display, e);
				}
				if (drawAI) {
					FileService.drawAI(display, e);
				}
				if (drawLocations) {
					FileService.drawLocations(display, e);
				}
				if (drawDoors) {
					FileService.drawDoors(display, e);
				}
				if (drawMaterials) {
					FileService.drawMaterials(display, e);
				}
				if (drawIdentifier) {
					FileService.drawIdentifier(display, e);
				}
				if (drawCoords) {
					combo.select(ScriptItems.COORDS.ordinal());
					combo.notifyListeners(SWT.Selection, new Event());
					FileService.drawCoords(display, e, text.getText());
				}
			}
		});
		sc.setContent(canvas);
		sc.setMinSize(imageWidth, imageHeight);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		scrolledCanvas = sc;
		
		Composite contentComposite = new Composite(mainSash, SWT.BORDER);
		contentComposite.setLayout(new GridLayout());
		
		//Establecer proporciones iniciales para el SashForm (55% mapa, 45% panel derecho)
		mainSash.setWeights(new int[] { 55, 45 });
		
		Composite undoRedoComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout undoRedoLayout = new GridLayout();
		undoRedoLayout.numColumns = 2;
		undoRedoComposite.setLayout(undoRedoLayout);
		GridData undoRedoData = new GridData(GridData.FILL_HORIZONTAL);
		undoRedoComposite.setLayoutData(undoRedoData);
		
		undoButton = new Button(undoRedoComposite, SWT.NONE);
		undoButton.setText("Undo (Ctrl+Z)");
		undoButton.setEnabled(false);
		undoButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            undo();
	        }
	    });
		
		redoButton = new Button(undoRedoComposite, SWT.NONE);
		redoButton.setText("Redo (Ctrl+Y)");
		redoButton.setEnabled(false);
		redoButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            redo();
	        }
	    });
		
	    //Crear contenedor (composite) para agrupar checkboxes horizontalmente
	    Composite checkBoxComposite1 = new Composite(contentComposite, SWT.NONE);
	    GridLayout checkLayout1 = new GridLayout();
	    checkLayout1.numColumns = 4;
	    checkLayout1.marginHeight = 0;
	    checkLayout1.marginWidth = 0;
	    checkBoxComposite1.setLayout(checkLayout1);
	    checkBoxComposite1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		Button checkBoxElements = new Button(checkBoxComposite1, SWT.CHECK);
		checkBoxElements.setText("Draw Elements");
		checkBoxElements.setSelection(drawElements);
		checkBoxElements.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawElements = btn.getSelection();
	            canvas.redraw();
	        }
	    });
		
	    Button checkBoxAnimations = new Button(checkBoxComposite1, SWT.CHECK);
	    checkBoxAnimations.setText("Draw Animations");
	    checkBoxAnimations.setSelection(drawAnimations);
	    checkBoxAnimations.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawAnimations = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    Button checkBoxIdentifier = new Button(checkBoxComposite1, SWT.CHECK);
	    checkBoxIdentifier.setText("Draw Identifier");
	    checkBoxIdentifier.setSelection(drawIdentifier);
	    checkBoxIdentifier.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawIdentifier = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    Button checkBoxCoords = new Button(checkBoxComposite1, SWT.CHECK);
	    checkBoxCoords.setText("Draw Coordinates");
	    checkBoxCoords.setSelection(drawCoords);
	    checkBoxCoords.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawCoords = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    // Segunda fila de checkboxes
	    Composite checkBoxComposite2 = new Composite(contentComposite, SWT.NONE);
	    GridLayout checkLayout2 = new GridLayout();
	    checkLayout2.numColumns = 4;
	    checkLayout2.marginHeight = 0;
	    checkLayout2.marginWidth = 0;
	    checkBoxComposite2.setLayout(checkLayout2);
	    checkBoxComposite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
		Button checkBoxObstacles = new Button(checkBoxComposite2, SWT.CHECK);
		checkBoxObstacles.setText("Draw Obstacles");
		checkBoxObstacles.setSelection(drawObstacles);
	    checkBoxObstacles.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawObstacles = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    Button checkBoxWaypoints = new Button(checkBoxComposite2, SWT.CHECK);
	    checkBoxWaypoints.setText("Draw Waypoints");
	    checkBoxWaypoints.setSelection(drawWaypoints);
	    checkBoxWaypoints.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawWaypoints = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    Button checkBoxAI = new Button(checkBoxComposite2, SWT.CHECK);
	    checkBoxAI.setText("Draw AI Zones");
	    checkBoxAI.setSelection(drawWaypoints);
	    checkBoxAI.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawAI = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    Button checkBoxLocations = new Button(checkBoxComposite2, SWT.CHECK);
	    checkBoxLocations.setText("Draw Locations");
	    checkBoxLocations.setSelection(drawLocations);
	    checkBoxLocations.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawLocations = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    // Tercera fila de checkboxes
	    Composite checkBoxComposite3 = new Composite(contentComposite, SWT.NONE);
	    GridLayout checkLayout3 = new GridLayout();
	    checkLayout3.numColumns = 2;
	    checkLayout3.marginHeight = 0;
	    checkLayout3.marginWidth = 0;
	    checkBoxComposite3.setLayout(checkLayout3);
	    checkBoxComposite3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
	    Button checkBoxDoors = new Button(checkBoxComposite3, SWT.CHECK);
	    checkBoxDoors.setText("Draw Doors");
	    checkBoxDoors.setSelection(drawDoors);
	    checkBoxDoors.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawDoors = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
	    Button checkBoxMaterials = new Button(checkBoxComposite3, SWT.CHECK);
	    checkBoxMaterials.setText("Draw Materials");
	    checkBoxMaterials.setSelection(drawMaterials);
	    checkBoxMaterials.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button) event.getSource();
	            drawMaterials = btn.getSelection();
	            canvas.redraw();
	        }
	    });
	    
		//Contenedor exclusivo para el buscador y navegación de resultados
		Composite searchComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout searchLayout = new GridLayout(5, false);
		searchLayout.marginWidth = 0;
		searchLayout.marginHeight = 0;
		searchComposite.setLayout(searchLayout);
		searchComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//Buscador que resalta coincidencias y permite navegar entre ellas
	    Label searchLabel = new Label(searchComposite, SWT.NONE);
	    searchLabel.setText("Search:");
	    
	    searchText = new Text(searchComposite, SWT.BORDER);
	    GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		searchText.setLayoutData(textData);
	    searchText.addModifyListener(new ModifyListener() {
	        @Override
	        public void modifyText(ModifyEvent e) {
	            String searchTerm = searchText.getText().toLowerCase();
	            highlightSearch(searchTerm);
	        }
	    });
		// Botones para navegar entre coincidencias de búsqueda
		Button prevButton = new Button(searchComposite, SWT.PUSH);
		prevButton.setText("◀");
		prevButton.addListener(SWT.Selection, e -> prevMatch());
		prevButton.setToolTipText("Previous match (Shift + Enter)");
		Button nextButton = new Button(searchComposite, SWT.PUSH);
		nextButton.setText("▶");
		nextButton.addListener(SWT.Selection, e -> nextMatch());
		nextButton.setToolTipText("Next match (Enter)");
		// Atajo de teclado para navegación: Enter para siguiente, Shift+Enter para anterior
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					if ((e.stateMask & SWT.SHIFT) != 0) {
						// 🔥 Shift + Enter → anterior
						prevMatch();
					} else {
						// 🔥 Enter → siguiente
						nextMatch();
					}
					e.doit = false; // evita beep / comportamiento raro
				}
			}
		});
		// Contador de coincidencias
		searchCount = new Label(searchComposite, SWT.NONE);
		GridData countData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		countData.widthHint = 70;
		searchCount.setLayoutData(countData);

	    combo = new Combo(contentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
	    combo.setItems(comboItems);
	    combo.select(0);
	    combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(combo.getSelectionIndex());
			}
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetSelected(combo.getSelectionIndex());
			}
			private void widgetSelected(int selectionIndex) {
				if (selectionIndex != activeComboItem) {
					// Guardar el estado actual de la sección anterior en comboTexts
					String currentContent = text.getText();
					comboTexts[activeComboItem] = currentContent;
					originalComboTexts[activeComboItem] = currentContent;
					textPositions[activeComboItem] = text.getTopIndex();
					// Guardar este cambio de sección en el historial
					saveToHistory();
					
					setConsoleText("");
					activeComboItem = selectionIndex;
					searchText.setText("");
					isRestoring = true;
					text.setText(comboTexts[selectionIndex]);
					isRestoring = false;
					text.setTopIndex(textPositions[selectionIndex]);
					isFirstChange = true;
				}
			}
	    });
	    
	    text = new StyledText(contentComposite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    text.setFont(new Font(display, new FontData("Courier New", 10, SWT.NORMAL)));
	    text.setLayoutData(new GridData(GridData.FILL_BOTH));
	    isRestoring = true;
	    text.setText(comboTexts[activeComboItem]);
	    isRestoring = false;
	    isFirstChange = true;
	    // Guardar estado inicial en historial
	    saveToHistory();
	    isFirstChange = true;  // Reset para que el próximo cambio también se guarde
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				if (!isRestoring) {
					if (isFirstChange) {
						isFirstChange = false;
						saveToHistory();
					}
					String currentContent = text.getText();
					comboTexts[activeComboItem] = currentContent;
					originalComboTexts[activeComboItem] = currentContent;
					text.redraw();
					saveToHistory();

					// Registrar cambio lógico de esta sección (1 solo cambio humano)
					registerLogicalTextChange();
					
					// Si estamos en ELEM y hay un elemento seleccionado, intentar actualizar el panel
					// PERO solo si el cambio vino desde el JSON, no desde el panel rápido.
					if (activeComboItem == ScriptItems.ELEM.ordinal() && currentElement != null && !isUpdatingFromPanel) {
						updatePanelFromJSON();
					}
				}
			}
		});
		
		text.addLineStyleListener(new LineStyleListener() {
			public void lineGetStyle(LineStyleEvent e) {
				if (activeComboItem == ScriptItems.WAYS.ordinal()) {
					LineStyles.setLineStyleWAYS(e, text);
				} else if (activeComboItem == ScriptItems.SCB.ordinal()) {
					LineStyles.setLineStyleSCB(e, text);
				}
		    }
		});
		
		installGlobalUndoRedoInterceptor(text);

		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'a' || e.keyCode == 'A')) {
					text.selectAll();
					e.doit = false;
				}
				setConsoleText("");
			}
		});
		
		textConsole = new StyledText(contentComposite, SWT.BORDER | SWT.H_SCROLL);
		installGlobalUndoRedoInterceptor(textConsole);
		textConsole.setFont(new Font(display, new FontData("Courier New", 10, SWT.NORMAL)));
		textConsole.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite coordsComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		coordsComposite.setLayout(gl);
		coordsComposite.setLayoutData(gd);
		
		textCoords = new StyledText(coordsComposite, SWT.BORDER);
		installGlobalUndoRedoInterceptor(textCoords);
		textCoords.setFont(new Font(display, new FontData("Courier New", 10, SWT.NORMAL)));
		textCoords.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label infoLabel = new Label(coordsComposite, SWT.NONE);
		infoLabel.setText("Element Info Panel:");
		GridData infoLabelData = new GridData(GridData.FILL_HORIZONTAL);
		infoLabelData.horizontalSpan = 2;
		infoLabel.setLayoutData(infoLabelData);
		
		// Grid para organizar los campos de elemento info
		Composite elementInfoGrid = new Composite(coordsComposite, SWT.NONE);
		GridLayout infoGridLayout = new GridLayout();
		infoGridLayout.numColumns = 8;
		infoGridLayout.marginHeight = 2;
		infoGridLayout.marginWidth = 0;
		elementInfoGrid.setLayout(infoGridLayout);
		GridData elementInfoGridData = new GridData(GridData.FILL_HORIZONTAL);
		elementInfoGridData.horizontalSpan = 2;
		elementInfoGrid.setLayoutData(elementInfoGridData);
		
		// Fila 1: identifier, x, y, direction
		Label labelId = new Label(elementInfoGrid, SWT.NONE);
		labelId.setText("identifier:");
		textElementId = new Text(elementInfoGrid, SWT.BORDER);
		textElementId.setLayoutData(new GridData(80, SWT.DEFAULT));
		textElementId.setEditable(false);
		
		Label labelX = new Label(elementInfoGrid, SWT.NONE);
		labelX.setText("x:");
		textElementX = new Text(elementInfoGrid, SWT.BORDER);
		textElementX.setLayoutData(new GridData(60, SWT.DEFAULT));
		textElementX.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentElement != null && !isRestoringElementInfo) {
					try {
						int x = Integer.parseInt(textElementX.getText());

						isUpdatingFromPanel = true;
						currentElement.setX((short) x);
						regenerateJSON();
						saveToHistory();
						markCurrentSectionAsChanged();
						isUpdatingFromPanel = false;

					} catch (NumberFormatException ex) {
						isUpdatingFromPanel = false;
						// Ignorar si no es un número válido
					}
				}
			}
		});
		
		Label labelY = new Label(elementInfoGrid, SWT.NONE);
		labelY.setText("y:");
		textElementY = new Text(elementInfoGrid, SWT.BORDER);
		textElementY.setLayoutData(new GridData(60, SWT.DEFAULT));
		textElementY.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentElement != null && !isRestoringElementInfo) {
					try {
						int y = Integer.parseInt(textElementY.getText());

						isUpdatingFromPanel = true;
						currentElement.setY((short) y);
						regenerateJSON();
						saveToHistory();
						markCurrentSectionAsChanged();
						isUpdatingFromPanel = false;

					} catch (NumberFormatException ex) {
						isUpdatingFromPanel = false;
						// Ignorar si no es un número válido
					}
				}
			}
		});
		
		Label labelDirection = new Label(elementInfoGrid, SWT.NONE);
		labelDirection.setText("direction:");
		textElementDirection = new Text(elementInfoGrid, SWT.BORDER);
		textElementDirection.setLayoutData(new GridData(50, SWT.DEFAULT));
		textElementDirection.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentElement != null && !isRestoringElementInfo && currentElement instanceof desperados.dvd.elements.Alive) {
					try {
						int direction = Integer.parseInt(textElementDirection.getText());

						isUpdatingFromPanel = true;
						((desperados.dvd.elements.Alive) currentElement).setDirection((byte) direction);
						regenerateJSON();
						saveToHistory();
						markCurrentSectionAsChanged();
						isUpdatingFromPanel = false;

					} catch (NumberFormatException ex) {
						isUpdatingFromPanel = false;
						// Ignorar si no es un número válido
					}
				}
			}
		});
		
		// Fila 2: dvf, sprite, character
		Label labelDvf = new Label(elementInfoGrid, SWT.NONE);
		labelDvf.setText("dvf:");
		textElementDvf = new Text(elementInfoGrid, SWT.BORDER);
		GridData dvfLabelData = new GridData(GridData.FILL_HORIZONTAL);
		textElementDvf.setLayoutData(dvfLabelData);
		textElementDvf.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentElement != null && !isRestoringElementInfo) {
					isUpdatingFromPanel = true;
					currentElement.setDvf(textElementDvf.getText());
					regenerateJSON();
					saveToHistory();
					markCurrentSectionAsChanged();
					isUpdatingFromPanel = false;
				}
			}
		});
		
		Label labelSprite = new Label(elementInfoGrid, SWT.NONE);
		labelSprite.setText("sprite:");
		textElementSprite = new Text(elementInfoGrid, SWT.BORDER);
		GridData spriteLabelData = new GridData(GridData.FILL_HORIZONTAL);
		textElementSprite.setLayoutData(spriteLabelData);
		textElementSprite.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentElement != null && !isRestoringElementInfo) {
					isUpdatingFromPanel = true;
					currentElement.setSprite(textElementSprite.getText());
					regenerateJSON();
					saveToHistory();
					markCurrentSectionAsChanged();
					isUpdatingFromPanel = false;
				}
			}
		});
		
		Label labelCharacter = new Label(elementInfoGrid, SWT.NONE);
		labelCharacter.setText("character:");
		textElementCharacter = new Text(elementInfoGrid, SWT.BORDER);
		GridData characterLabelData = new GridData(GridData.FILL_HORIZONTAL);
		textElementCharacter.setLayoutData(characterLabelData);
		textElementCharacter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentElement != null && !isRestoringElementInfo && currentElement instanceof desperados.dvd.elements.NPC) {
					desperados.dvd.elements.NPC npc = (desperados.dvd.elements.NPC) currentElement;
					String charValue = textElementCharacter.getText().trim();
					try {
						// Obtener la clase del enum Character dentro de NPC
						java.lang.Class<?> charClass = Class.forName("desperados.dvd.elements.NPC$Character");
						// Obtener todos los valores del enum
						Object[] enumConstants = charClass.getEnumConstants();
						// Buscar el que coincida con lo que el usuario escribió
						for (Object enumConstant : enumConstants) {
							if (enumConstant.toString().equalsIgnoreCase(charValue)) {
								// Obtener el setter y llamarlo
								java.lang.reflect.Method setter = npc.getClass().getMethod("setCharacter", charClass);

								isUpdatingFromPanel = true;
								setter.invoke(npc, enumConstant);
								regenerateJSON();
								saveToHistory();
								markCurrentSectionAsChanged();
								isUpdatingFromPanel = false;

								return;
							}
						}
					} catch (Exception ex) {
						isUpdatingFromPanel = false;
						// Ignorar si hay error en la conversión
					}
				}
			}
		});
		
		// Interceptar Ctrl+Z / Ctrl+Y en todos los campos del panel rápido
		installGlobalUndoRedoInterceptor(textElementId);
		installGlobalUndoRedoInterceptor(textElementX);
		installGlobalUndoRedoInterceptor(textElementY);
		installGlobalUndoRedoInterceptor(textElementDirection);
		installGlobalUndoRedoInterceptor(textElementDvf);
		installGlobalUndoRedoInterceptor(textElementSprite);
		installGlobalUndoRedoInterceptor(textElementCharacter);

		spriteLabel = new Label(coordsComposite, SWT.CENTER | SWT.BORDER);
		GridData spriteImageLabelData = new GridData(GridData.FILL_HORIZONTAL);
		spriteImageLabelData.heightHint = 80;
		spriteImageLabelData.horizontalSpan = 2;
		spriteLabel.setLayoutData(spriteImageLabelData);
		spriteLabel.setText("Sprite Preview");

		DragSource dragSource = new DragSource(spriteLabel, DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dragSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = currentElement != null ? currentElement.getIdentifier() : null;
				}
			}
		});
		
		Composite navComposite = new Composite(coordsComposite, SWT.NONE);
		GridLayout navLayout = new GridLayout();
		navLayout.numColumns = 2;
		navComposite.setLayout(navLayout);
		GridData navData = new GridData(GridData.FILL_HORIZONTAL);
		navData.horizontalSpan = 2;
		navComposite.setLayoutData(navData);
		
		prevElementButton = new Button(navComposite, SWT.NONE);
		prevElementButton.setText("< Previous Element");
		prevElementButton.setEnabled(false);
		prevElementButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent event) {
		        previousElement();
		    }
		});
		
		nextElementButton = new Button(navComposite, SWT.NONE);
		nextElementButton.setText("Next Element >");
		nextElementButton.setEnabled(false);
		nextElementButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent event) {
		        nextElement();
		    }
		});
		
		//Contenedor exclusivo para los botones guardar cambios, ejecutar juego y volver atrás
		Composite updateRunBackComposite = new Composite(contentComposite, SWT.NONE);
		updateRunBackComposite.setLayout(new GridLayout(3, false));
		updateRunBackComposite.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, true, false));
		// Escribir cambios en el archivo
	    Button buttonUpdate = new Button(updateRunBackComposite, SWT.NONE);
		buttonUpdate.setText("Write Current Section To File");
		buttonUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setConsoleText("");

				if (!hasUnsavedChangesInCurrentSection()) {
					MessageBox noChangesBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
					noChangesBox.setText("No Changes");
					noChangesBox.setMessage("There are no unsaved changes in the current section.");
					noChangesBox.open();
					return;
				}

				int logicalCount = getUnsavedLogicalChangeCount();
				String changeSummary = buildCurrentChangesSummary();

				String message = "Write current section to file?\n\n"
						+ "Unsaved logical changes detected: " + logicalCount;

				if (changeSummary != null && !changeSummary.trim().isEmpty()) {
					message += "\n\nChanges:\n" + changeSummary;
				}

				int result = openScrollableConfirmWriteDialog(
					"Confirm Write",
					"Write current section to file?",
					logicalCount,
					changeSummary
				);

				if (result != SWT.YES) {
					resyncCurrentElementFromCurrentJson();
					return;
				}
				
				if (activeComboItem == ScriptItems.ELEM.ordinal()) {
					writeElementsToDvd();
				} else if (activeComboItem == ScriptItems.WAYS.ordinal()) {
					writeWaypointsToDvd();
				} else if (activeComboItem == ScriptItems.SCRP.ordinal()) {
					writeLocationsToDvd();
				} else if (activeComboItem == ScriptItems.SCB.ordinal()) {
					writeScriptToScb();
				} else if (activeComboItem == ScriptItems.BUIL.ordinal()) {
					writeBuildingsToDvd();
				}

				// Si se escribió, esta sección pasa a ser el nuevo estado guardado
				markCurrentSectionAsSaved();
			}
		});
		// Ejecutar juego
		Button runGameButton = new Button(updateRunBackComposite, SWT.PUSH);
		runGameButton.setText("Run Game");
		runGameButton.addListener(SWT.Selection, e -> runGame());
		// Volver al inicio
	    Button backButton = new Button(updateRunBackComposite, SWT.PUSH);
		backButton.setText("Back");
		backButton.addListener(SWT.Selection, e -> {
			int result = confirmSaveChanges();

			// 🔥 cancelar SI NO es YES ni NO
			// if (result != SWT.YES && result != SWT.NO) {
			// 	return;
			// }

			if (result == SWT.YES) {
				// 🔥 MISMA LÓGICA QUE WRITE
				if (activeComboItem == ScriptItems.ELEM.ordinal()) {
					writeElementsToDvd();
				} else if (activeComboItem == ScriptItems.WAYS.ordinal()) {
					writeWaypointsToDvd();
				} else if (activeComboItem == ScriptItems.SCRP.ordinal()) {
					writeLocationsToDvd();
				} else if (activeComboItem == ScriptItems.SCB.ordinal()) {
					writeScriptToScb();
				} else if (activeComboItem == ScriptItems.BUIL.ordinal()) {
					writeBuildingsToDvd();
				}

				markCurrentSectionAsSaved();
			}

			// SWT.NO → descarta
			goBackToStart();
		});

	    //Establecer tamaño y posición del shell
	    shell.setSize(1400, 900);
	    shell.setLocation(100, 100);
	}

	private void runGame() {
		if (gameDir == null || exeName == null) {
			MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
			box.setText("Warning");
			box.setMessage("Game directory not set!");
			box.open();
			return;
		}

		try {
			boolean launched = false;
			// Prioridad: Steam si hay AppID
			if (steamAppId != null && !steamAppId.isEmpty()) {
				String steamUrl = "steam://run/" + steamAppId;
				launched = Program.launch(steamUrl);
			}

			// 🔥 Si Steam falló → usar exe
			if (!launched) {
				ProcessBuilder pb = new ProcessBuilder(exeName);
				pb.directory(new File(gameDir));
				pb.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void goBackToStart() {
		try {
			stopAnimationLoop();

			shell.dispose();

			shell = new Shell(Display.getDefault());
			shell.setText(appName + " " + appVersion);

			createContentsEmpty(Display.getDefault());

			shell.open();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int confirmSaveChanges() {
		if (!hasUnsavedChangesInCurrentSection()) {
			return SWT.YES; // no hay cambios → seguir como si aceptara
		}

		int logicalCount = getUnsavedLogicalChangeCount();
		String summary = buildCurrentChangesSummary();

		int result = openScrollableConfirmWriteDialog(
			"Unsaved Changes",
			"You have unsaved changes.\n\nDo you want to SAVE them before leaving?",
			logicalCount,
			summary
		);

		return result; // SWT.YES, SWT.NO, SWT.CANCEL
	}
	private void startAnimationLoop() {
		if (animationRunning) return;

		animationRunning = true;

		Runnable animator = new Runnable() {
			@Override
			public void run() {

				// 🔥 detener si ya no corresponde
				if (!animationRunning || canvas == null || canvas.isDisposed()) {
					animationRunning = false;
					return;
				}

				if (drawAnimations || drawElements) {
					canvas.redraw();
				}

				// 🔁 siguiente frame
				Display.getDefault().timerExec(200, this);
			}
		};

		Display.getDefault().timerExec(200, animator);
	}
	private void stopAnimationLoop() {
		animationRunning = false;
	}

	private int openScrollableConfirmWriteDialog(String title, String question, int logicalCount, String changeSummary) {
		final int[] result = new int[] { SWT.CANCEL };

		Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
		dialog.setText(title);
		dialog.setLayout(new GridLayout(1, false));

		// Pregunta principal
		Label questionLabel = new Label(dialog, SWT.WRAP);
		questionLabel.setText(question != null ? question : "");
		questionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Conteo
		Label countLabel = new Label(dialog, SWT.WRAP);
		countLabel.setText("Unsaved logical changes detected: " + logicalCount);
		countLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Título del resumen
		if (changeSummary != null && !changeSummary.trim().isEmpty()) {
			Label changesLabel = new Label(dialog, SWT.NONE);
			changesLabel.setText("Changes:");
			changesLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			Text changesTextArea = new Text(dialog, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY | SWT.WRAP);
			changesTextArea.setText(changeSummary);
			changesTextArea.setEditable(false);

			GridData changesGd = new GridData(SWT.FILL, SWT.FILL, true, true);
			changesGd.widthHint = 560;
			changesGd.heightHint = 280;
			changesTextArea.setLayoutData(changesGd);

			changesTextArea.setTopIndex(0);
		}

		// Barra de botones
		Composite buttonBar = new Composite(dialog, SWT.NONE);
		buttonBar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));

		GridLayout buttonLayout = new GridLayout(2, true);
		buttonLayout.marginWidth = 0;
		buttonLayout.marginHeight = 0;
		buttonLayout.horizontalSpacing = 10;
		buttonBar.setLayout(buttonLayout);

		Button yesButton = new Button(buttonBar, SWT.PUSH);
		yesButton.setText("Yes");
		yesButton.setLayoutData(new GridData(100, SWT.DEFAULT));

		Button noButton = new Button(buttonBar, SWT.PUSH);
		noButton.setText("No");
		noButton.setLayoutData(new GridData(100, SWT.DEFAULT));

		yesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result[0] = SWT.YES;
				dialog.close();
			}
		});

		noButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result[0] = SWT.NO;
				dialog.close();
			}
		});

		dialog.addListener(SWT.Close, e -> {
			if (result[0] != SWT.YES && result[0] != SWT.NO) {
				result[0] = SWT.NO;
			}
		});

		dialog.setDefaultButton(yesButton);
		dialog.setSize(620, 480);
		dialog.setLocation(
			shell.getLocation().x + (shell.getSize().x - 620) / 2,
			shell.getLocation().y + (shell.getSize().y - 480) / 2
		);

		dialog.open();

		Display display = shell.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return result[0];
	}

	private void moveCurrentElement(int dx, int dy) {
		try {
			isKeyboardMoving = true;

			int newX = Math.max(0, Math.min(32767, currentElement.getX() + dx));
			int newY = Math.max(0, Math.min(32767, currentElement.getY() + dy));

			currentElement.setX((short)newX);
			currentElement.setY((short)newY);

			// Solo UI (fluido)
			canvas.redraw();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void cloneElementAt(String sourceId, int x, int y) {
		String currentText = comboTexts[ScriptItems.ELEM.ordinal()];
		if (currentText == null || currentText.isEmpty()) return;

		// 🔍 buscar bloque JSON del elemento original
		String sourceBlock = extractElementBlock(currentText, sourceId);
		if (sourceBlock == null) return;

		// 🆕 nuevo ID
		int newId = findNextElementId(currentText);
		String newIdentifier = "Element_" + newId;

		// ✏️ reemplazar identifier + posición
		String newBlock = sourceBlock
			.replaceFirst("\"identifier\"\\s*:\\s*\"" + Pattern.quote(sourceId) + "\"",
						"\"identifier\" : \"" + newIdentifier + "\"")
			.replaceAll("\"x\"\\s*:\\s*\\d+", "\"x\" : " + x)
			.replaceAll("\"y\"\\s*:\\s*\\d+", "\"y\" : " + y);

		// ➕ insertar en JSON
		String updated = insertAtEndOfElemArray(currentText, newBlock);

		setComboText(ScriptItems.ELEM.ordinal(), updated);

		// 🔥 seleccionar automáticamente
		selectNewElementFromJson(newIdentifier);
	}

	private String extractElementBlock(String text, String identifier) {
		Pattern p = Pattern.compile("\\{[^\\{\\}]*\"identifier\"\\s*:\\s*\"" 
			+ Pattern.quote(identifier) + "\"[^\\{\\}]*\\}");
		
		Matcher m = p.matcher(text);
		if (m.find()) {
			return m.group();
		}
		return null;
	}

	private int findLastElementId() {
		String text = comboTexts[ScriptItems.ELEM.ordinal()];
		Matcher m = Pattern.compile("Element_(\\d+)").matcher(text);

		int max = -1;
		while (m.find()) {
			int id = Integer.parseInt(m.group(1));
			if (id > max) max = id;
		}
		return max;
	}

	private String getCurrentSectionKey() {
		switch (activeComboItem) {
			case 0: return "ELEM";
			case 1: return "WAYS";
			case 2: return "SCRP";
			case 3: return "BUIL";
			case 4: return "SCB";
			case 5: return "COORDS";
			default: return "UNKNOWN";
		}
	}

	private String getCurrentElementKey() {
		if (activeComboItem != ScriptItems.ELEM.ordinal()) {
			return "NO_ELEMENT";
		}
		if (currentElement == null) {
			return "NO_ELEMENT";
		}
		String id = currentElement.getIdentifier();
		return (id != null && !id.trim().isEmpty()) ? id.trim() : "NO_ELEMENT";
	}

	private void registerLogicalFieldChange(String fieldName) {
		String key = getCurrentSectionKey() + "|" + getCurrentElementKey() + "|" + fieldName;

		String currentSectionText = comboTexts[activeComboItem] != null ? comboTexts[activeComboItem] : "";
		String savedSectionText = lastSavedTexts[activeComboItem] != null ? lastSavedTexts[activeComboItem] : "";

		boolean differsFromSaved = !currentSectionText.equals(savedSectionText);

		if (differsFromSaved) {
			pendingLogicalChanges.add(key);
		} else {
			pendingLogicalChanges.remove(key);
		}

		updateDirtyUI();
	}

	private void registerLogicalTextChange() {
		String sectionKey = getCurrentSectionKey();
		String key = sectionKey + ":text";

		String current = comboTexts[activeComboItem];
		String saved = lastSavedTexts[activeComboItem];

		if (current == null) current = "";
		if (saved == null) saved = "";

		if (current.equals(saved)) {
			pendingLogicalChanges.remove(key);
		} else {
			pendingLogicalChanges.add(key);
		}

		updateDirtyUI();
	}

	private void rebuildLogicalChangesAgainstSavedState() {
		// Si ELEM quedó exactamente igual al último guardado, eliminar todos los cambios lógicos ELEM
		int elemIdx = ScriptItems.ELEM.ordinal();
		String currentElemText = comboTexts[elemIdx] != null ? comboTexts[elemIdx] : "";
		String savedElemText = lastSavedTexts[elemIdx] != null ? lastSavedTexts[elemIdx] : "";

		if (currentElemText.equals(savedElemText)) {
			pendingLogicalChanges.removeIf(k -> k.startsWith("ELEM:"));
		}
	}

	private boolean hasUnsavedChanges() {
		for (int i = 0; i < comboTexts.length; i++) {
			String current = comboTexts[i] != null ? comboTexts[i] : "";
			String saved = lastSavedTexts[i] != null ? lastSavedTexts[i] : "";
			if (!current.equals(saved)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasUnsavedChangesInCurrentSection() {
		String current = comboTexts[activeComboItem] != null ? comboTexts[activeComboItem] : "";
		String saved = lastSavedTexts[activeComboItem] != null ? lastSavedTexts[activeComboItem] : "";
		return !current.equals(saved);
	}

	private int countLogicalChangesInElemSection() {
		String currentJson = comboTexts[ScriptItems.ELEM.ordinal()] != null ? comboTexts[ScriptItems.ELEM.ordinal()] : "";
		String savedJson = lastSavedTexts[ScriptItems.ELEM.ordinal()] != null ? lastSavedTexts[ScriptItems.ELEM.ordinal()] : "";

		// Si son idénticos, no hay cambios
		if (currentJson.equals(savedJson)) {
			return 0;
		}

		try {
			// Parsear elementos del JSON guardado
			FileService.readElementsFromString(savedJson);
			List<desperados.dvd.elements.Element> savedElements = new ArrayList<>(FileService.getElements());

			// Parsear elementos del JSON actual
			FileService.readElementsFromString(currentJson);
			List<desperados.dvd.elements.Element> currentElements = new ArrayList<>(FileService.getElements());

			// Restaurar el estado actual en FileService para no romper nada visualmente
			FileService.readElementsFromString(currentJson);

			java.util.Map<String, desperados.dvd.elements.Element> savedMap = new java.util.HashMap<>();
			java.util.Map<String, desperados.dvd.elements.Element> currentMap = new java.util.HashMap<>();

			for (desperados.dvd.elements.Element e : savedElements) {
				if (e.getIdentifier() != null) {
					savedMap.put(e.getIdentifier(), e);
				}
			}

			for (desperados.dvd.elements.Element e : currentElements) {
				if (e.getIdentifier() != null) {
					currentMap.put(e.getIdentifier(), e);
				}
			}

			java.util.Set<String> allIds = new java.util.HashSet<>();
			allIds.addAll(savedMap.keySet());
			allIds.addAll(currentMap.keySet());

			int changes = 0;

			for (String id : allIds) {
				desperados.dvd.elements.Element oldElem = savedMap.get(id);
				desperados.dvd.elements.Element newElem = currentMap.get(id);

				// elemento agregado o eliminado
				if (oldElem == null || newElem == null) {
					changes++;
					continue;
				}

				// Campos base
				if (!safeEquals(oldElem.getIdentifier(), newElem.getIdentifier())) changes++;
				if (!safeEquals(oldElem.getDvf(), newElem.getDvf())) changes++;
				if (!safeEquals(oldElem.getSprite(), newElem.getSprite())) changes++;
				if (oldElem.getX() != newElem.getX()) changes++;
				if (oldElem.getY() != newElem.getY()) changes++;

				// direction si es Alive
				boolean oldAlive = oldElem instanceof desperados.dvd.elements.Alive;
				boolean newAlive = newElem instanceof desperados.dvd.elements.Alive;
				if (oldAlive != newAlive) {
					changes++;
				} else if (oldAlive && newAlive) {
					byte oldDir = ((desperados.dvd.elements.Alive) oldElem).getDirection();
					byte newDir = ((desperados.dvd.elements.Alive) newElem).getDirection();
					if (oldDir != newDir) changes++;
				}

				// character si es NPC
				boolean oldNpc = oldElem instanceof desperados.dvd.elements.NPC;
				boolean newNpc = newElem instanceof desperados.dvd.elements.NPC;
				if (oldNpc != newNpc) {
					changes++;
				} else if (oldNpc && newNpc) {
					Object oldChar = null;
					Object newChar = null;
					try {
						java.lang.reflect.Method getter = desperados.dvd.elements.NPC.class.getMethod("getCharacter");
						oldChar = getter.invoke((desperados.dvd.elements.NPC) oldElem);
						newChar = getter.invoke((desperados.dvd.elements.NPC) newElem);
					} catch (Exception ex) {
						// ignorar
					}
					if (!safeEquals(oldChar, newChar)) changes++;
				}
			}

			return changes;

		} catch (Exception e) {
			// Si el JSON está temporalmente inválido mientras el usuario escribe,
			// no inventamos conteos raros. Como fallback, 1 cambio.
			try {
				FileService.readElementsFromString(currentJson);
			} catch (Exception ignored) {}
			return 1;
		}
	}

	private boolean safeEquals(Object a, Object b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}

	private int getUnsavedLogicalChangeCount() {
		if (!hasUnsavedChanges()) {
			return 0;
		}

		int total = 0;

		for (int i = 0; i < comboTexts.length; i++) {
			String current = comboTexts[i] != null ? comboTexts[i] : "";
			String saved = lastSavedTexts[i] != null ? lastSavedTexts[i] : "";

			if (current.equals(saved)) {
				continue;
			}

			// ELEM: contar cambios lógicos por campo
			if (i == ScriptItems.ELEM.ordinal()) {
				total += countLogicalChangesInElemSection();
			} else {
				// Otras secciones: por ahora 1 cambio por sección modificada
				total += 1;
			}
		}

		return total;
	}

	private void markCurrentSectionAsSaved() {
		lastSavedTexts[activeComboItem] = comboTexts[activeComboItem];

		String currentSectionPrefix = getCurrentSectionKey() + ":";
		pendingLogicalChanges.removeIf(k -> k.startsWith(currentSectionPrefix));

		updateDirtyUI();
	}

	private void updateDirtyUI() {
		int count = getUnsavedLogicalChangeCount();
		boolean hasChanges = hasUnsavedChanges();

		String baseTitle = appName + " " + appVersion;
		if (hasChanges) {
			shell.setText(baseTitle + " * (" + count + " unsaved change" + (count == 1 ? "" : "s") + ")");
		} else {
			shell.setText(baseTitle);
		}
	}

	private void copyToClipboard(String string) {
		TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new Object[] { string }, new Transfer[] { textTransfer });
	}

	public void setConsoleText(String text) {
		textConsole.setText(text);
	}

	private void markCurrentSectionAsChanged() {
		if (activeComboItem >= 0 && activeComboItem < unsavedChanges.length) {
			unsavedChanges[activeComboItem]++;
		}
	}

	private void resetCurrentSectionUnsavedChanges() {
		if (activeComboItem >= 0 && activeComboItem < unsavedChanges.length) {
			unsavedChanges[activeComboItem] = 0;
		}
	}

	private int getCurrentSectionUnsavedChanges() {
		if (activeComboItem >= 0 && activeComboItem < unsavedChanges.length) {
			return unsavedChanges[activeComboItem];
		}
		return 0;
	}

	private boolean hasCurrentSectionRealChanges() {
		return getCurrentSectionUnsavedChanges() > 0;
	}

	private String getCurrentSectionFriendlyName() {
		if (activeComboItem == ScriptItems.ELEM.ordinal()) {
			return "Elements (ELEM)";
		} else if (activeComboItem == ScriptItems.WAYS.ordinal()) {
			return "Waypoints (WAYS)";
		} else if (activeComboItem == ScriptItems.SCRP.ordinal()) {
			return "Locations (SCRP)";
		} else if (activeComboItem == ScriptItems.SCB.ordinal()) {
			return "Mission Script (SCB)";
		} else if (activeComboItem == ScriptItems.BUIL.ordinal()) {
			return "Buildings / Doors (BUIL)";
		} else if (activeComboItem == ScriptItems.COORDS.ordinal()) {
			return "Coordinate Parser";
		}
		return combo.getText();
	}

	private void writeElementsToDvd() {
		try {
			// Guardar el ID del elemento actual antes de escribir
			String currentElementId = (currentElement != null) ? currentElement.getIdentifier() : null;
			
			FileService.writeElementsFromStringToDvd(text.getText());
			setConsoleText("Writing ELEM section to DVD completed!");
			resetCurrentSectionUnsavedChanges();
			
			// Restaurar la referencia a currentElement desde la lista actualizada
			if (currentElementId != null) {
				List<desperados.dvd.elements.Element> elements = FileService.getElements();
				for (desperados.dvd.elements.Element elem : elements) {
					if (elem.getIdentifier().equals(currentElementId)) {
						currentElement = elem;
						// Recargar la información en los campos
						displayElementInfo(elem);
						updateNavigationButtons();
						break;
					}
				}
			}
			
			canvas.redraw();
		} catch (ServiceException e) {
			setConsoleText(e.getMessage());
		}
		
	}

	private void writeWaypointsToDvd() {
		WaypointParser parser = new WaypointParser(text.getText());
    	List<WaypointRoute> routes = parser.parseText();
    	
    	if (parser.hasErrors()) {
			setConsoleText(parser.getErrorMessage());
			return;
		}
    	
		if (routes != null) {
			try {
				FileService.writeWaypointsToDvd(routes);
				canvas.redraw();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setConsoleText("Writing WAYS section to DVD completed!");
			resetCurrentSectionUnsavedChanges();
		}
	}

	private void writeLocationsToDvd() {
		try {
			FileService.writeLocationsFromStringToDvd(text.getText());
			canvas.redraw();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		setConsoleText("Writing SCRP section to DVD completed!");
		resetCurrentSectionUnsavedChanges();
	}

	private void writeBuildingsToDvd() {
		setConsoleText("Not implemented yet!");
	}

	private void writeScriptToScb() {
		ScbParser parser = new ScbParser(text.getText());
		parser.parseText();
		
		if (parser.hasErrors()) {
			setConsoleText(parser.getErrorMessage());
			return;
		}
		
		try {
			FileService.writeScriptToScb(parser.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setConsoleText("Writing mission script to SCB completed!");
		resetCurrentSectionUnsavedChanges();
	}

	// Buscador nuevo, que resalta las coincidencias en amarillo sin modificar el texto
	private void highlightSearch(String searchTerm) {
		searchMatches.clear();
		currentMatchIndex = -1;

		text.setStyleRanges(new StyleRange[0]); // limpiar resaltado

		if (searchTerm == null || searchTerm.isEmpty()) {
			searchCount.setText("0/0");
			return;
		}

		String content = text.getText().toLowerCase();
		int index = 0;

		java.util.List<StyleRange> styles = new java.util.ArrayList<>();

		while ((index = content.indexOf(searchTerm, index)) >= 0) {
			searchMatches.add(index);

			StyleRange style = new StyleRange();
			style.start = index;
			style.length = searchTerm.length();
			style.background = org.eclipse.swt.widgets.Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);

			styles.add(style);

			index += searchTerm.length();
		}

		text.setStyleRanges(styles.toArray(new StyleRange[0]));

		// 🔥 seleccionar el primero automáticamente
		if (!searchMatches.isEmpty()) {
			currentMatchIndex = 0;
			goToMatch(0);
		}

		updateSearchCounter();
	}
	private void goToMatch(int index) {
		int pos = searchMatches.get(index);

		text.setSelection(pos, pos + searchText.getText().length());
		text.showSelection();

		int line = text.getLineAtOffset(pos);
		text.setTopIndex(Math.max(0, line - 5));
	}
	private void prevMatch() {
		if (searchMatches.isEmpty()) return;

		currentMatchIndex--;
		if (currentMatchIndex < 0) {
			currentMatchIndex = searchMatches.size() - 1;
		}

		goToMatch(currentMatchIndex);
		updateSearchCounter();
	}
	private void nextMatch() {
		if (searchMatches.isEmpty()) return;

		currentMatchIndex = (currentMatchIndex + 1) % searchMatches.size();
		goToMatch(currentMatchIndex);
		updateSearchCounter();
	}
	private void updateSearchCounter() {
		int total = searchMatches.size();

		if (total == 0) {
			searchCount.setText("0/0");
		} else {
			searchCount.setText((currentMatchIndex + 1) + "/" + total);
		}
	}

	private void saveToHistory() {
		// Remove any states after current position (branching)
		while (historyStack.size() > historyIndex + 1) {
			historyStack.remove(historyStack.size() - 1);
			historyCaret.remove(historyCaret.size() - 1);
			historyScroll.remove(historyScroll.size() - 1);
			historyActiveItem.remove(historyActiveItem.size() - 1);
		}
		
		// Create a copy of current comboTexts state
		String[] state = new String[comboTexts.length];
		for (int i = 0; i < comboTexts.length; i++) {
			state[i] = comboTexts[i];
		}
		
		// Save caret and scroll position
		int caret = text != null ? text.getCaretOffset() : 0;
		int scroll = text != null ? text.getTopIndex() : 0;
		
		// Add new state to history
		historyStack.add(state);
		historyCaret.add(caret);
		historyScroll.add(scroll);
		historyActiveItem.add(activeComboItem);
		historyIndex++;
		
		// Limit history size to MAX_HISTORY
		if (historyStack.size() > MAX_HISTORY) {
			historyStack.remove(0);
			historyCaret.remove(0);
			historyScroll.remove(0);
			historyActiveItem.remove(0);
			historyIndex--;
		}
		
		updateUndoRedoButtons();
	}

	private void undo() {
		if (historyIndex > 0) {
			historyIndex--;
			restoreState(historyIndex);
			updateUndoRedoButtons();
			text.setFocus();
		}
	}

	private void redo() {
		if (historyIndex < historyStack.size() - 1) {
			historyIndex++;
			restoreState(historyIndex);
			updateUndoRedoButtons();
			text.setFocus();
		}
	}

	private void restoreState(int index) {
		String[] state = historyStack.get(index);
		int caretPos = historyCaret.get(index);
		int scrollPos = historyScroll.get(index);
		int restoredActiveItem = historyActiveItem.get(index);
		
		// Restore all comboTexts and originalComboTexts
		for (int i = 0; i < comboTexts.length; i++) {
			comboTexts[i] = state[i];
			originalComboTexts[i] = state[i];
		}
		
		// Change to the section where this change was made
		if (restoredActiveItem != activeComboItem) {
			activeComboItem = restoredActiveItem;
			combo.select(activeComboItem);
		}
		
		// Update the displayed text
		isRestoring = true;
		text.setText(comboTexts[activeComboItem]);
		
		// Restore cursor and scroll position
		try {
			if (caretPos >= 0 && caretPos <= text.getCharCount()) {
				text.setCaretOffset(caretPos);
			} else {
				text.setCaretOffset(0);
			}
		} catch (Exception e) {
			text.setCaretOffset(0);
		}
		
		try {
			text.setTopIndex(scrollPos);
		} catch (Exception e) {
			text.setTopIndex(0);
		}
		
		isRestoring = false;
		
		// Si volvemos a ELEM, actualizar los elementos desde el JSON restaurado
		if (activeComboItem == ScriptItems.ELEM.ordinal()) {
			try {
				FileService.readElementsFromString(comboTexts[activeComboItem]);
				// Restaurar la referencia a currentElement con el elemento actualizado
				if (currentElement != null) {
					String currentElementId = currentElement.getIdentifier();
					List<desperados.dvd.elements.Element> elements = FileService.getElements();
					for (desperados.dvd.elements.Element elem : elements) {
						if (elem.getIdentifier().equals(currentElementId)) {
							currentElement = elem;
							// Actualizar los campos del panel con el nuevo valor
							displayElementInfo(elem);
							break;
						}
					}
				}
			} catch (ServiceException e) {
				// Ignorar errores
			}
		}

		registerLogicalTextChange();
		rebuildLogicalChangesAgainstSavedState();
		updateDirtyUI();
	}

	private void resyncCurrentElementFromCurrentJson() {
		if (activeComboItem != ScriptItems.ELEM.ordinal()) {
			return;
		}

		try {
			String currentElementId = (currentElement != null) ? currentElement.getIdentifier() : null;

			// Reparsear el JSON actual visible
			FileService.readElementsFromString(comboTexts[activeComboItem]);

			if (currentElementId != null) {
				List<desperados.dvd.elements.Element> elements = FileService.getElements();
				for (desperados.dvd.elements.Element elem : elements) {
					if (elem.getIdentifier().equals(currentElementId)) {
						currentElement = elem;
						FileService.setSelectedElement(elem);

						isRestoringElementInfo = true;
						displayElementInfo(elem);
						isRestoringElementInfo = false;
						return;
					}
				}
			}
		} catch (ServiceException e) {
			// ignorar
		}
	}

	private void updateUndoRedoButtons() {
		if (undoButton != null) {
			undoButton.setEnabled(historyIndex > 0);
		}
		if (redoButton != null) {
			redoButton.setEnabled(historyIndex < historyStack.size() - 1);
		}
	}

	private String buildCurrentChangesSummary() {
		if (activeComboItem < 0 || activeComboItem >= comboTexts.length) {
			return "";
		}

		String savedText = lastSavedTexts[activeComboItem] != null ? lastSavedTexts[activeComboItem] : "";
		String currentText = comboTexts[activeComboItem] != null ? comboTexts[activeComboItem] : "";

		if (savedText.equals(currentText)) {
			return "";
		}

		String sectionName = getSectionDisplayName(activeComboItem);

		switch (activeComboItem) {
			case 0: // ELEM
				return buildSectionHeader(sectionName) + buildElemDiffSummary(savedText, currentText);

			case 1: // WAYS
				return buildSectionHeader(sectionName) + buildWaysDiffSummary(savedText, currentText);

			case 2: // SCRP
				return buildSectionHeader(sectionName) + buildScrpDiffSummary(savedText, currentText);

			case 3: // BUIL
				return buildSectionHeader(sectionName) + buildBuilDiffSummary(savedText, currentText);

			case 4: // SCB
				return buildSectionHeader(sectionName) + buildScbDiffSummary(savedText, currentText);

			default:
				return buildSectionHeader(sectionName) + buildGenericLogicalSummary(savedText, currentText);
		}
	}

	private String getSectionDisplayName(int comboIndex) {
		switch (comboIndex) {
			case 0: return "ELEM";
			case 1: return "WAYS";
			case 2: return "SCRP";
			case 3: return "BUIL";
			case 4: return "SCB";
			default: return "UNKNOWN";
		}
	}

	private String buildSectionHeader(String sectionName) {
		return "[" + sectionName + "]\n";
	}

	private String buildGenericLogicalSummary(String oldText, String newText) {
		if (oldText == null) oldText = "";
		if (newText == null) newText = "";

		if (oldText.equals(newText)) {
			return "- No field-level summary available\n";
		}

		return "- Logical changes detected in this section\n";
	}

	private String buildElemDiffSummary(String oldText, String newText) {
		java.util.Map<String, java.util.Map<String, String>> oldMap = parseElemBlocks(oldText);
		java.util.Map<String, java.util.Map<String, String>> newMap = parseElemBlocks(newText);

		Set<String> allIds = new LinkedHashSet<>();
		allIds.addAll(oldMap.keySet());
		allIds.addAll(newMap.keySet());

		StringBuilder sb = new StringBuilder();
		int shown = 0;
		final int LIMIT = 30;

		for (String id : allIds) {
			java.util.Map<String, String> oldFields = oldMap.get(id);
			java.util.Map<String, String> newFields = newMap.get(id);

			if (oldFields == null) {
				sb.append("- ").append(id).append("\n");
				sb.append("   • added\n\n");
				shown++;
			} else if (newFields == null) {
				sb.append("- ").append(id).append("\n");
				sb.append("   • removed\n\n");
				shown++;
			} else {
				Set<String> allFields = new LinkedHashSet<>();
				allFields.addAll(oldFields.keySet());
				allFields.addAll(newFields.keySet());

				StringBuilder elementBlock = new StringBuilder();
				int elementChanges = 0;

				for (String field : allFields) {
					String oldVal = oldFields.get(field);
					String newVal = newFields.get(field);

					if (oldVal == null) oldVal = "";
					if (newVal == null) newVal = "";

					if (!oldVal.equals(newVal)) {
						if (elementChanges == 0) {
							elementBlock.append("- ").append(id).append("\n");
						}

						elementBlock.append("   • ")
								.append(field)
								.append(": ")
								.append(formatDiffValue(oldVal))
								.append(" → ")
								.append(formatDiffValue(newVal))
								.append("\n");

						elementChanges++;
						shown++;

						if (shown >= LIMIT) {
							sb.append(elementBlock);
							sb.append("   • ...more changes...\n");
							return sb.toString().trim();
						}
					}
				}

				if (elementChanges > 0) {
					sb.append(elementBlock).append("\n");
				}
			}

			if (shown >= LIMIT) {
				sb.append("- ...more changes...\n");
				break;
			}
		}

		return sb.toString().trim();
	}

	private String buildWaysDiffSummary(String oldText, String newText) {
		java.util.Map<String, String> oldRoutes = splitWaysRoutesByIdentifier(oldText);
		java.util.Map<String, String> newRoutes = splitWaysRoutesByIdentifier(newText);

		StringBuilder sb = new StringBuilder();

		java.util.Set<String> allIds = new java.util.TreeSet<String>();
		allIds.addAll(oldRoutes.keySet());
		allIds.addAll(newRoutes.keySet());

		for (String routeId : allIds) {
			boolean inOld = oldRoutes.containsKey(routeId);
			boolean inNew = newRoutes.containsKey(routeId);

			if (!inOld && inNew) {
				sb.append("- Route added: ").append(routeId).append("\n");
				continue;
			}

			if (inOld && !inNew) {
				sb.append("- Route removed: ").append(routeId).append("\n");
				continue;
			}

			String oldRoute = oldRoutes.get(routeId);
			String newRoute = newRoutes.get(routeId);

			String routeDetails = buildSingleWaysRouteDiff(routeId, oldRoute, newRoute);
			if (!routeDetails.isEmpty()) {
				sb.append(routeDetails).append("\n");
			}
		}

		if (sb.length() == 0) {
			return "- No route-level differences detected";
		}

		return sb.toString().trim();
	}

	private String buildScrpDiffSummary(String oldText, String newText) {
		java.util.List<java.util.Map<String, String>> oldLocations = parseScrpLocations(oldText);
		java.util.List<java.util.Map<String, String>> newLocations = parseScrpLocations(newText);

		StringBuilder sb = new StringBuilder();
		int max = Math.max(oldLocations.size(), newLocations.size());

		int shown = 0;
		final int LIMIT = 40;

		for (int i = 0; i < max; i++) {
			java.util.Map<String, String> oldLoc = i < oldLocations.size() ? oldLocations.get(i) : null;
			java.util.Map<String, String> newLoc = i < newLocations.size() ? newLocations.get(i) : null;

			if (oldLoc == null && newLoc != null) {
				sb.append("- Location #").append(i).append("\n");
				sb.append("   • added\n\n");
				shown++;
				if (shown >= LIMIT) break;
				continue;
			}

			if (oldLoc != null && newLoc == null) {
				sb.append("- Location #").append(i).append("\n");
				sb.append("   • removed\n\n");
				shown++;
				if (shown >= LIMIT) break;
				continue;
			}

			String singleDiff = buildSingleScrpLocationDiff(i, oldLoc, newLoc);
			if (!singleDiff.isEmpty()) {
				sb.append(singleDiff).append("\n\n");
				shown++;
				if (shown >= LIMIT) break;
			}
		}

		if (sb.length() == 0) {
			return "- Logical changes detected in SCRP\n";
		}

		if (shown >= LIMIT && max > LIMIT) {
			sb.append("... more changes not shown\n");
		}

		return sb.toString().trim();
	}

	private String buildBuilDiffSummary(String oldText, String newText) {
		java.util.List<String> oldBlocks = extractBuildingBlocks(oldText);
		java.util.List<String> newBlocks = extractBuildingBlocks(newText);

		StringBuilder sb = new StringBuilder();

		int max = Math.max(oldBlocks.size(), newBlocks.size());
		int shown = 0;
		final int LIMIT = 40;

		for (int i = 0; i < max; i++) {
			String oldB = i < oldBlocks.size() ? oldBlocks.get(i) : null;
			String newB = i < newBlocks.size() ? newBlocks.get(i) : null;

			if (oldB == null && newB != null) {
				sb.append("- Building #").append(i).append("\n");
				sb.append("   • added\n\n");
				shown++;
				if (shown >= LIMIT) break;
				continue;
			}

			if (oldB != null && newB == null) {
				sb.append("- Building #").append(i).append("\n");
				sb.append("   • removed\n\n");
				shown++;
				if (shown >= LIMIT) break;
				continue;
			}

			String fineDiff = buildSingleBuildingDiff(i, oldB, newB);
			if (!fineDiff.isEmpty()) {
				sb.append(fineDiff).append("\n\n");
				shown++;
				if (shown >= LIMIT) break;
			}
		}

		if (sb.length() == 0) {
			return "- No visible structural changes in BUIL\n";
		}

		if (shown >= LIMIT && max > LIMIT) {
			sb.append("... more changes not shown\n");
		}

		return sb.toString().trim();
	}

	private String buildScbDiffSummary(String oldText, String newText) {
		java.util.List<String> oldLines = splitLines(oldText);
		java.util.List<String> newLines = splitLines(newText);

		java.util.List<ScbDiffEntry> diffs = computeLineDiffWithContext(oldLines, newLines);

		StringBuilder sb = new StringBuilder();

		String currentClass = null;
		String currentFunction = null;

		int shown = 0;
		final int LIMIT = 120;

		for (ScbDiffEntry d : diffs) {

			// imprimir class si cambia
			if (!safeEquals(currentClass, d.className)) {
				currentClass = d.className;
				currentFunction = null;

				sb.append("Class ").append(currentClass != null ? currentClass : "?").append("\n");
			}

			// imprimir function si cambia
			if (!safeEquals(currentFunction, d.functionName)) {
				currentFunction = d.functionName;

				sb.append("  Function ").append(currentFunction != null ? currentFunction : "?").append("\n\n");
			}

			sb.append("   - Line ").append(d.lineNumber).append("\n");
			sb.append("      • ").append(d.text).append("\n\n");

			shown++;
			if (shown >= LIMIT) {
				sb.append("... more changes not shown\n");
				break;
			}
		}

		if (sb.length() == 0) {
			return "- No changes detected in SCB\n";
		}

		return sb.toString().trim();
	}

	private java.util.List<ScbDiffEntry> computeLineDiffWithContext(
			java.util.List<String> oldLines,
			java.util.List<String> newLines) {

		int m = oldLines.size();
		int n = newLines.size();

		int[][] dp = new int[m + 1][n + 1];

		for (int i = m - 1; i >= 0; i--) {
			for (int j = n - 1; j >= 0; j--) {
				if (safeEquals(oldLines.get(i), newLines.get(j))) {
					dp[i][j] = dp[i + 1][j + 1] + 1;
				} else {
					dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
				}
			}
		}

		java.util.List<ScbDiffEntry> result = new java.util.ArrayList<>();

		String currentClass = null;
		String currentFunction = null;

		int i = 0, j = 0;

		while (i < m && j < n) {
			String o = oldLines.get(i);
			String nLine = newLines.get(j);

			// actualizar contexto
			if (isClassLine(nLine)) currentClass = extractClassName(nLine);
			if (isFunctionLine(nLine)) currentFunction = extractFunctionName(nLine);

			if (safeEquals(o, nLine)) {
				i++;
				j++;
			} else if (dp[i + 1][j] >= dp[i][j + 1]) {
				result.add(new ScbDiffEntry(
					i + 1,
					"removed: " + formatDiffValue(o),
					currentClass,
					currentFunction
				));
				i++;
			} else {
				result.add(new ScbDiffEntry(
					j + 1,
					"added: " + formatDiffValue(nLine),
					currentClass,
					currentFunction
				));
				j++;
			}
		}

		while (i < m) {
			result.add(new ScbDiffEntry(
				i + 1,
				"removed: " + formatDiffValue(oldLines.get(i)),
				currentClass,
				currentFunction
			));
			i++;
		}

		while (j < n) {
			result.add(new ScbDiffEntry(
				j + 1,
				"added: " + formatDiffValue(newLines.get(j)),
				currentClass,
				currentFunction
			));
			j++;
		}

		return result;
	}

	private boolean isClassLine(String line) {
		return line != null && line.startsWith("Class ");
	}

	private String extractClassName(String line) {
		return line.replace("Class", "").trim();
	}

	private boolean isFunctionLine(String line) {
		return line != null && line.startsWith("Function ");
	}

	private String extractFunctionName(String line) {
		String name = line.replace("Function", "").trim();
		int idx = name.indexOf("(");
		return idx >= 0 ? name.substring(0, idx) : name;
	}

	private java.util.List<String> computeLineDiff(java.util.List<String> oldLines, java.util.List<String> newLines) {
		int m = oldLines.size();
		int n = newLines.size();

		int[][] dp = new int[m + 1][n + 1];

		// LCS
		for (int i = m - 1; i >= 0; i--) {
			for (int j = n - 1; j >= 0; j--) {
				if (safeEquals(oldLines.get(i), newLines.get(j))) {
					dp[i][j] = dp[i + 1][j + 1] + 1;
				} else {
					dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
				}
			}
		}

		java.util.List<String> result = new java.util.ArrayList<>();

		int i = 0, j = 0;

		while (i < m && j < n) {
			String o = oldLines.get(i);
			String nLine = newLines.get(j);

			if (safeEquals(o, nLine)) {
				i++;
				j++;
			} else if (dp[i + 1][j] >= dp[i][j + 1]) {
				result.add(formatRemoved(i, o));
				i++;
			} else {
				result.add(formatAdded(j, nLine));
				j++;
			}
		}

		while (i < m) {
			result.add(formatRemoved(i, oldLines.get(i)));
			i++;
		}

		while (j < n) {
			result.add(formatAdded(j, newLines.get(j)));
			j++;
		}

		return result;
	}

	private String formatAdded(int index, String line) {
		return "- Line " + (index + 1) + "\n   • added: " + formatDiffValue(line);
	}

	private String formatRemoved(int index, String line) {
		return "- Line " + (index + 1) + "\n   • removed: " + formatDiffValue(line);
	}

	private java.util.List<String> splitLines(String text) {
		java.util.List<String> result = new java.util.ArrayList<>();

		if (text == null) return result;

		String[] lines = text.split("\\r?\\n");
		for (String l : lines) {
			result.add(l.trim());
		}

		return result;
	}

	private java.util.List<String> extractBuildingBlocks(String text) {
		java.util.List<String> result = new java.util.ArrayList<>();

		if (text == null || text.trim().isEmpty()) {
			return result;
		}

		String[] lines = text.split("\\r?\\n");
		StringBuilder current = null;

		for (String line : lines) {
			if (line.startsWith("Building (")) {
				if (current != null) {
					result.add(current.toString().trim());
				}
				current = new StringBuilder();
			}

			if (current != null) {
				current.append(line).append("\n");
			}
		}

		if (current != null) {
			result.add(current.toString().trim());
		}

		return result;
	}

	private String buildSingleBuildingDiff(int index, String oldBlock, String newBlock) {
		java.util.Map<String, Object> oldB = parseBuilding(oldBlock);
		java.util.Map<String, Object> newB = parseBuilding(newBlock);

		StringBuilder sb = new StringBuilder();
		int changes = 0;

		// type
		String oldType = (String) oldB.get("type");
		String newType = (String) newB.get("type");

		if (!safeEquals(oldType, newType)) {
			appendBuildingHeader(sb, index, changes++);
			sb.append("   • type: ").append(oldType).append(" → ").append(newType).append("\n");
		}

		// occupants
		String oldOcc = (String) oldB.get("occupants");
		String newOcc = (String) newB.get("occupants");

		if (!safeEquals(oldOcc, newOcc)) {
			appendBuildingHeader(sb, index, changes++);
			sb.append("   • occupants: ").append(oldOcc).append(" → ").append(newOcc).append("\n");
		}

		// IDs (ultra fino)
		java.util.List<String> oldIdBlocks = extractIdBlocks(oldBlock);
		java.util.List<String> newIdBlocks = extractIdBlocks(newBlock);

		int max = Math.max(oldIdBlocks.size(), newIdBlocks.size());

		for (int i = 0; i < max; i++) {
			String o = i < oldIdBlocks.size() ? oldIdBlocks.get(i) : null;
			String n = i < newIdBlocks.size() ? newIdBlocks.get(i) : null;

			if (o == null && n != null) {
				appendBuildingHeader(sb, index, changes++);
				sb.append("   • ").append(extractIdLabel(n)).append(" added\n");
				continue;
			}

			if (o != null && n == null) {
				appendBuildingHeader(sb, index, changes++);
				sb.append("   • ").append(extractIdLabel(o)).append(" removed\n");
				continue;
			}

			String idDiff = buildSingleIdDiff(o, n);
			if (!idDiff.isEmpty()) {
				appendBuildingHeader(sb, index, changes++);
				sb.append(idDiff);
			}
		}

		return sb.toString().trim();
	}

	private java.util.List<String> extractIdBlocks(String buildingBlock) {
		java.util.List<String> result = new java.util.ArrayList<>();

		if (buildingBlock == null) return result;

		String[] lines = buildingBlock.split("\\r?\\n");
		StringBuilder current = null;

		for (String line : lines) {
			if (line.startsWith("ID:")) {
				if (current != null) {
					result.add(current.toString().trim());
				}
				current = new StringBuilder();
			}

			if (current != null) {
				current.append(line).append("\n");
			}
		}

		if (current != null) {
			result.add(current.toString().trim());
		}

		return result;
	}

	private String extractIdLabel(String block) {
		if (block == null) return "ID";

		String firstLine = block.split("\\r?\\n")[0]; // ID: x,y
		return firstLine.replace("ID:", "ID").trim();
	}

	private String buildSingleIdDiff(String oldBlock, String newBlock) {
		StringBuilder sb = new StringBuilder();

		String label = extractIdLabel(oldBlock);

		java.util.List<String> oldLines = java.util.Arrays.asList(oldBlock.split("\\r?\\n"));
		java.util.List<String> newLines = java.util.Arrays.asList(newBlock.split("\\r?\\n"));

		// FLAGS (línea después de "Door:")
		String oldFlags = extractAfter(oldLines, "Door:");
		String newFlags = extractAfter(newLines, "Door:");

		if (!safeEquals(oldFlags, newFlags)) {
			sb.append("   • ").append(label).append("\n");
			sb.append("      - Door flags: ").append(oldFlags).append(" → ").append(newFlags).append("\n");
		}

		// RECT (4 líneas después de flags)
		java.util.List<String> oldRect = extractBlockAfter(oldLines, oldFlags, 4);
		java.util.List<String> newRect = extractBlockAfter(newLines, newFlags, 4);

		int maxRect = Math.max(oldRect.size(), newRect.size());

		for (int i = 0; i < maxRect; i++) {
			String o = i < oldRect.size() ? oldRect.get(i) : null;
			String n = i < newRect.size() ? newRect.get(i) : null;

			if (!safeEquals(o, n)) {
				if (sb.length() == 0) {
					sb.append("   • ").append(label).append("\n");
				}
				sb.append("      - rect[").append(i).append("]: ")
				.append(formatDiffValue(o)).append(" → ")
				.append(formatDiffValue(n)).append("\n");
			}
		}

		// SLOTS (líneas restantes)
		java.util.List<String> oldSlots = extractRemaining(oldLines, oldFlags, 5);
		java.util.List<String> newSlots = extractRemaining(newLines, newFlags, 5);

		int maxSlots = Math.max(oldSlots.size(), newSlots.size());

		for (int i = 0; i < maxSlots; i++) {
			String o = i < oldSlots.size() ? oldSlots.get(i) : null;
			String n = i < newSlots.size() ? newSlots.get(i) : null;

			if (!safeEquals(o, n)) {
				if (sb.length() == 0) {
					sb.append("   • ").append(label).append("\n");
				}
				sb.append("      - slot[").append(i).append("]: ")
				.append(formatDiffValue(o)).append(" → ")
				.append(formatDiffValue(n)).append("\n");
			}
		}

		return sb.toString();
	}

	private String extractAfter(java.util.List<String> lines, String marker) {
		for (int i = 0; i < lines.size() - 1; i++) {
			if (lines.get(i).trim().equals(marker)) {
				return lines.get(i + 1).trim();
			}
		}
		return "";
	}

	private java.util.List<String> extractBlockAfter(java.util.List<String> lines, String anchor, int count) {
		java.util.List<String> result = new java.util.ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).trim().equals(anchor)) {
				for (int j = 1; j <= count && i + j < lines.size(); j++) {
					result.add(lines.get(i + j).trim());
				}
				break;
			}
		}

		return result;
	}

	private java.util.List<String> extractRemaining(java.util.List<String> lines, String anchor, int skip) {
		java.util.List<String> result = new java.util.ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).trim().equals(anchor)) {
				for (int j = skip; i + j < lines.size(); j++) {
					String l = lines.get(i + j).trim();
					if (!l.isEmpty()) {
						result.add(l);
					}
				}
				break;
			}
		}

		return result;
	}

	private java.util.Map<String, Object> parseBuilding(String block) {
		java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();

		if (block == null) return result;

		String[] lines = block.split("\\r?\\n");

		// Header
		if (lines.length > 0) {
			String header = lines[0]; // Building (@xxxx): 5, 0
			result.put("header", header);

			int colon = header.indexOf(":");
			if (colon >= 0) {
				String[] parts = header.substring(colon + 1).split(",");
				if (parts.length >= 2) {
					result.put("type", parts[0].trim());
					result.put("unk", parts[1].trim());
				}
			}
		}

		// occupants
		for (String line : lines) {
			if (line.contains("occupants")) {
				result.put("occupants", line.trim());
				break;
			}
		}

		// IDs
		java.util.List<String> ids = new java.util.ArrayList<>();
		for (String line : lines) {
			if (line.startsWith("ID:")) {
				ids.add(line.substring(3).trim());
			}
		}
		result.put("ids", ids);

		// store full block for fallback
		result.put("raw", block.trim());

		return result;
	}

	private void appendBuildingHeader(StringBuilder sb, int index, int changes) {
		if (changes == 0) {
			sb.append("- Building #").append(index).append("\n");
		}
	}

	private String buildSingleScrpLocationDiff(int index, java.util.Map<String, String> oldLoc, java.util.Map<String, String> newLoc) {
		if (oldLoc == null || newLoc == null) {
			return "";
		}

		java.util.Set<String> allFields = new java.util.LinkedHashSet<String>();
		allFields.addAll(oldLoc.keySet());
		allFields.addAll(newLoc.keySet());

		StringBuilder block = new StringBuilder();
		int changes = 0;

		for (String field : allFields) {
			String oldVal = oldLoc.get(field);
			String newVal = newLoc.get(field);

			if (oldVal == null) oldVal = "";
			if (newVal == null) newVal = "";

			if (!oldVal.equals(newVal)) {
				if (changes == 0) {
					block.append("- Location #").append(index).append("\n");
				}

				block.append("   • ")
					.append(field)
					.append(": ")
					.append(formatDiffValue(oldVal))
					.append(" → ")
					.append(formatDiffValue(newVal))
					.append("\n");

				changes++;
			}
		}

		return block.toString().trim();
	}

	private java.util.List<java.util.Map<String, String>> parseScrpLocations(String text) {
		java.util.List<java.util.Map<String, String>> result = new java.util.ArrayList<java.util.Map<String, String>>();

		if (text == null || text.trim().isEmpty()) {
			return result;
		}

		try {
			com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
			com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(text);

			com.fasterxml.jackson.databind.JsonNode locationsNode = root.get("locations");
			if (locationsNode == null || !locationsNode.isArray()) {
				return result;
			}

			for (com.fasterxml.jackson.databind.JsonNode locNode : locationsNode) {
				java.util.Map<String, String> fields = new java.util.LinkedHashMap<String, String>();

				// identifier
				fields.put("identifier", getJsonTextValue(locNode, "identifier"));

				// classname
				String classname = getJsonTextValue(locNode, "classname");
				fields.put("classname", classname);

				// unknown1 / unknown2
				fields.put("unknown1", getJsonTextValue(locNode, "unknown1"));
				fields.put("unknown2", getJsonTextValue(locNode, "unknown2"));

				// points
				com.fasterxml.jackson.databind.JsonNode pointsNode = locNode.get("points");
				if (pointsNode != null && pointsNode.isArray()) {
					fields.put("points count", String.valueOf(pointsNode.size()));

					for (int i = 0; i < pointsNode.size(); i++) {
						com.fasterxml.jackson.databind.JsonNode p = pointsNode.get(i);

						String x = getJsonTextValue(p, "x");
						String y = getJsonTextValue(p, "y");

						fields.put("point[" + i + "]", "(" + x + ", " + y + ")");
					}
				} else {
					fields.put("points count", "0");
				}

				result.add(fields);
			}
		} catch (Exception e) {
			// fallback silencioso para no romper el guardado
		}

		return result;
	}

	private String getJsonTextValue(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
		if (node == null || fieldName == null) {
			return "";
		}

		com.fasterxml.jackson.databind.JsonNode child = node.get(fieldName);
		if (child == null || child.isNull()) {
			return "";
		}

		if (child.isTextual()) {
			return child.asText();
		}

		return child.toString();
	}

	private String buildSingleWaysRouteDiff(String routeId, String oldRoute, String newRoute) {
		if (oldRoute == null) oldRoute = "";
		if (newRoute == null) newRoute = "";

		String normalizedOld = normalizeWaysRouteBlock(oldRoute);
		String normalizedNew = normalizeWaysRouteBlock(newRoute);

		if (normalizedOld.equals(normalizedNew)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("- ").append(routeId).append("\n");

		java.util.List<String> oldWaypoints = extractWaypointBlocks(oldRoute);
		java.util.List<String> newWaypoints = extractWaypointBlocks(newRoute);

		if (oldWaypoints.size() != newWaypoints.size()) {
			sb.append("   • waypoint count: ")
			.append(oldWaypoints.size())
			.append(" → ")
			.append(newWaypoints.size())
			.append("\n");
		}

		int max = Math.max(oldWaypoints.size(), newWaypoints.size());

		for (int i = 0; i < max; i++) {
			String oldWp = i < oldWaypoints.size() ? oldWaypoints.get(i) : null;
			String newWp = i < newWaypoints.size() ? newWaypoints.get(i) : null;

			if (oldWp == null && newWp != null) {
				sb.append("   • Waypoint #").append(i).append(" added\n");
				appendIndentedWaypointPreview(sb, newWp, "      ");
				continue;
			}

			if (oldWp != null && newWp == null) {
				sb.append("   • Waypoint #").append(i).append(" removed\n");
				appendIndentedWaypointPreview(sb, oldWp, "      ");
				continue;
			}

			String wpDiff = buildSingleWaypointDiff(i, oldWp, newWp);
			if (!wpDiff.isEmpty()) {
				sb.append(wpDiff);
			}
		}

		return sb.toString().trim();
	}

	private String buildSingleWaypointDiff(int waypointIndex, String oldWp, String newWp) {
		if (oldWp == null) oldWp = "";
		if (newWp == null) newWp = "";

		String normalizedOld = normalizeWaysRouteBlock(oldWp);
		String normalizedNew = normalizeWaysRouteBlock(newWp);

		if (normalizedOld.equals(normalizedNew)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("   • Waypoint #").append(waypointIndex).append("\n");

		String oldGoto = extractWaypointGotoPos(oldWp);
		String newGoto = extractWaypointGotoPos(newWp);

		if (!safeEquals(oldGoto, newGoto)) {
			sb.append("      - GotoPos: ")
			.append(formatDiffValue(oldGoto))
			.append(" → ")
			.append(formatDiffValue(newGoto))
			.append("\n");
		}

		String oldClassname = extractWaypointClassname(oldWp);
		String newClassname = extractWaypointClassname(newWp);

		if (!safeEquals(oldClassname, newClassname)) {
			sb.append("      - Classname: ")
			.append(formatDiffValue(oldClassname))
			.append(" → ")
			.append(formatDiffValue(newClassname))
			.append("\n");
		}

		String sectionDiff = buildWaypointSectionsDiff(oldWp, newWp);
		if (!sectionDiff.isEmpty()) {
			sb.append(sectionDiff);
		}

		return sb.toString();
	}

	private String buildWaypointSectionsDiff(String oldWp, String newWp) {
		java.util.Map<String, String> oldSections = extractTopLevelSections(oldWp);
		java.util.Map<String, String> newSections = extractTopLevelSections(newWp);

		java.util.Set<String> allSectionKeys = new java.util.TreeSet<String>();
		allSectionKeys.addAll(oldSections.keySet());
		allSectionKeys.addAll(newSections.keySet());

		StringBuilder sb = new StringBuilder();

		for (String sectionKey : allSectionKeys) {
			String oldSection = oldSections.get(sectionKey);
			String newSection = newSections.get(sectionKey);

			if (oldSection == null && newSection != null) {
				sb.append("      - Section(").append(sectionKey).append(") added\n");
				appendIndentedStructuredBlock(sb, newSection, "         ");
				continue;
			}

			if (oldSection != null && newSection == null) {
				sb.append("      - Section(").append(sectionKey).append(") removed\n");
				appendIndentedStructuredBlock(sb, oldSection, "         ");
				continue;
			}

			String subsectionDiff = buildSubsectionDiff(sectionKey, oldSection, newSection);
			if (!subsectionDiff.isEmpty()) {
				sb.append(subsectionDiff);
			}
		}

		return sb.toString();
	}

	private String buildSubsectionDiff(String sectionKey, String oldSection, String newSection) {
		java.util.Map<String, String> oldSubsections = extractSubsections(oldSection);
		java.util.Map<String, String> newSubsections = extractSubsections(newSection);

		java.util.Set<String> allSubKeys = new java.util.TreeSet<String>();
		allSubKeys.addAll(oldSubsections.keySet());
		allSubKeys.addAll(newSubsections.keySet());

		StringBuilder sb = new StringBuilder();

		for (String subKey : allSubKeys) {
			String oldSub = oldSubsections.get(subKey);
			String newSub = newSubsections.get(subKey);

			if (oldSub == null && newSub != null) {
				sb.append("      - Section(").append(sectionKey).append(") / Subsection(").append(subKey).append(") added\n");
				appendIndentedStructuredBlock(sb, newSub, "         ");
				continue;
			}

			if (oldSub != null && newSub == null) {
				sb.append("      - Section(").append(sectionKey).append(") / Subsection(").append(subKey).append(") removed\n");
				appendIndentedStructuredBlock(sb, oldSub, "         ");
				continue;
			}

			String lineDiff = buildLineLevelDiff(sectionKey, subKey, oldSub, newSub);
			if (!lineDiff.isEmpty()) {
				sb.append(lineDiff);
			}
		}

		return sb.toString();
	}

	private String buildLineLevelDiff(String sectionKey, String subKey, String oldSub, String newSub) {
		java.util.List<String> oldLines = extractMeaningfulInnerLines(oldSub);
		java.util.List<String> newLines = extractMeaningfulInnerLines(newSub);

		int max = Math.max(oldLines.size(), newLines.size());
		StringBuilder block = new StringBuilder();

		for (int i = 0; i < max; i++) {
			String oldLine = i < oldLines.size() ? oldLines.get(i) : null;
			String newLine = i < newLines.size() ? newLines.get(i) : null;

			if (safeEquals(oldLine, newLine)) {
				continue;
			}

			if (block.length() == 0) {
				block.append("      - Section(")
					.append(sectionKey)
					.append(") / Subsection(")
					.append(subKey)
					.append(")\n");
			}

			block.append(buildPrettyCommandDiffLine(i + 1, oldLine, newLine));
		}

		return block.toString();
	}

	private String buildPrettyCommandDiffLine(int lineNumber, String oldLine, String newLine) {
		StringBuilder sb = new StringBuilder();

		if (oldLine == null && newLine != null) {
			ParsedWayCommand newCmd = parseWayCommand(newLine);

			if (newCmd != null) {
				sb.append("         • ")
				.append(newCmd.name)
				.append(" added: ")
				.append(formatDiffValue(formatWayCommandArgs(newCmd.args)))
				.append("\n");
			} else {
				sb.append("         • line ")
				.append(lineNumber)
				.append(" added: ")
				.append(formatDiffValue(newLine))
				.append("\n");
			}

			return sb.toString();
		}

		if (oldLine != null && newLine == null) {
			ParsedWayCommand oldCmd = parseWayCommand(oldLine);

			if (oldCmd != null) {
				sb.append("         • ")
				.append(oldCmd.name)
				.append(" removed: ")
				.append(formatDiffValue(formatWayCommandArgs(oldCmd.args)))
				.append("\n");
			} else {
				sb.append("         • line ")
				.append(lineNumber)
				.append(" removed: ")
				.append(formatDiffValue(oldLine))
				.append("\n");
			}

			return sb.toString();
		}

		ParsedWayCommand oldCmd = parseWayCommand(oldLine);
		ParsedWayCommand newCmd = parseWayCommand(newLine);

		if (oldCmd != null && newCmd != null) {
			if (safeEquals(oldCmd.name, newCmd.name)) {
				sb.append("         • ")
				.append(oldCmd.name)
				.append(": ")
				.append(formatDiffValue(formatWayCommandArgs(oldCmd.args)))
				.append(" → ")
				.append(formatDiffValue(formatWayCommandArgs(newCmd.args)))
				.append("\n");
			} else {
				sb.append("         • command: ")
				.append(formatDiffValue(oldLine))
				.append(" → ")
				.append(formatDiffValue(newLine))
				.append("\n");
			}
		} else {
			sb.append("         • line ")
			.append(lineNumber)
			.append(": ")
			.append(formatDiffValue(oldLine))
			.append(" → ")
			.append(formatDiffValue(newLine))
			.append("\n");
		}

		return sb.toString();
	}

	private ParsedWayCommand parseWayCommand(String line) {
		if (line == null) {
			return null;
		}

		String trimmed = line.trim();
		if (trimmed.isEmpty()) {
			return null;
		}

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
			"^([A-Za-z_][A-Za-z0-9_]*)\\s*\\((.*)\\)\\s*;?$"
		);
		java.util.regex.Matcher matcher = pattern.matcher(trimmed);

		if (!matcher.matches()) {
			return null;
		}

		String name = matcher.group(1).trim();
		String args = matcher.group(2).trim();

		return new ParsedWayCommand(name, args);
	}

	private String formatWayCommandArgs(String args) {
		if (args == null || args.trim().isEmpty()) {
			return "∅";
		}
		return args.trim();
	}

	private java.util.Map<String, String> extractTopLevelSections(String waypointBlock) {
		java.util.Map<String, String> result = new java.util.LinkedHashMap<String, String>();

		if (waypointBlock == null || waypointBlock.trim().isEmpty()) {
			return result;
		}

		int depth = 0;
		int index = 0;

		while (index < waypointBlock.length()) {
			char c = waypointBlock.charAt(index);

			if (c == '{') {
				depth++;
				index++;
				continue;
			}
			if (c == '}') {
				depth--;
				index++;
				continue;
			}

			if (depth == 1 && waypointBlock.startsWith("Section(", index)) {
				int numStart = index + "Section(".length();
				int numEnd = waypointBlock.indexOf(")", numStart);
				if (numEnd < 0) break;

				String sectionId = waypointBlock.substring(numStart, numEnd).trim();

				int openBrace = waypointBlock.indexOf("{", numEnd);
				if (openBrace < 0) break;

				int closeBrace = findMatchingBrace(waypointBlock, openBrace);
				if (closeBrace < 0) break;

				String fullBlock = waypointBlock.substring(index, closeBrace + 1).trim();
				result.put(sectionId, fullBlock);

				index = closeBrace + 1;
				continue;
			}

			index++;
		}

		return result;
	}

	private java.util.Map<String, String> extractSubsections(String sectionBlock) {
		java.util.Map<String, String> result = new java.util.LinkedHashMap<String, String>();

		if (sectionBlock == null || sectionBlock.trim().isEmpty()) {
			return result;
		}

		int depth = 0;
		int index = 0;

		while (index < sectionBlock.length()) {
			char c = sectionBlock.charAt(index);

			if (c == '{') {
				depth++;
				index++;
				continue;
			}
			if (c == '}') {
				depth--;
				index++;
				continue;
			}

			if (depth == 1 && sectionBlock.startsWith("Subsection(", index)) {
				int numStart = index + "Subsection(".length();
				int numEnd = sectionBlock.indexOf(")", numStart);
				if (numEnd < 0) break;

				String subsectionId = sectionBlock.substring(numStart, numEnd).trim();

				int openBrace = sectionBlock.indexOf("{", numEnd);
				if (openBrace < 0) break;

				int closeBrace = findMatchingBrace(sectionBlock, openBrace);
				if (closeBrace < 0) break;

				String fullBlock = sectionBlock.substring(index, closeBrace + 1).trim();
				result.put(subsectionId, fullBlock);

				index = closeBrace + 1;
				continue;
			}

			index++;
		}

		return result;
	}

	private java.util.List<String> extractMeaningfulInnerLines(String block) {
		java.util.List<String> lines = new java.util.ArrayList<String>();

		if (block == null || block.trim().isEmpty()) {
			return lines;
		}

		String normalized = normalizeWaysRouteBlock(block);

		normalized = normalized.replaceFirst("^[A-Za-z]+\\s*\\([^\\)]*\\)\\s*\\{\\s*", "");

		if (normalized.endsWith("}")) {
			normalized = normalized.substring(0, normalized.length() - 1).trim();
		}

		String[] rawLines = normalized.split("\\r?\\n");

		for (String line : rawLines) {
			String trimmed = line.trim();

			if (trimmed.isEmpty()) continue;
			if (trimmed.equals("{")) continue;
			if (trimmed.equals("}")) continue;

			lines.add(trimmed);
		}

		return lines;
	}

	private void appendIndentedStructuredBlock(StringBuilder sb, String block, String indent) {
		if (block == null || block.trim().isEmpty()) {
			return;
		}

		java.util.List<String> lines = extractMeaningfulInnerLinesPreserveHeaders(block);

		for (String line : lines) {
			sb.append(indent).append("• ").append(line).append("\n");
		}
	}

	private java.util.List<String> extractMeaningfulInnerLinesPreserveHeaders(String block) {
		java.util.List<String> lines = new java.util.ArrayList<String>();

		if (block == null || block.trim().isEmpty()) {
			return lines;
		}

		String[] rawLines = block.split("\\r?\\n");

		for (String line : rawLines) {
			String trimmed = line.trim();

			if (trimmed.isEmpty()) continue;
			if (trimmed.equals("{")) continue;
			if (trimmed.equals("}")) continue;

			lines.add(trimmed);
		}

		return lines;
	}

	private java.util.List<String> extractWaypointBlocks(String routeBlock) {
		java.util.List<String> waypoints = new java.util.ArrayList<String>();

		if (routeBlock == null || routeBlock.trim().isEmpty()) {
			return waypoints;
		}

		int index = 0;

		while (true) {
			int wpStart = routeBlock.indexOf("Waypoint()", index);
			if (wpStart < 0) {
				break;
			}

			int openBrace = routeBlock.indexOf("{", wpStart);
			if (openBrace < 0) {
				break;
			}

			int closeBrace = findMatchingBrace(routeBlock, openBrace);
			if (closeBrace < 0) {
				break;
			}

			String waypointBlock = routeBlock.substring(wpStart, closeBrace + 1).trim();
			waypoints.add(waypointBlock);

			index = closeBrace + 1;
		}

		return waypoints;
	}

	private String extractWaypointGotoPos(String waypointBlock) {
		if (waypointBlock == null || waypointBlock.trim().isEmpty()) {
			return "";
		}

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
			"GotoPos\\s*\\(([^\\)]*)\\)\\s*;"
		);
		java.util.regex.Matcher matcher = pattern.matcher(waypointBlock);

		if (matcher.find()) {
			return matcher.group(1).trim();
		}

		return "";
	}

	private String extractWaypointClassname(String waypointBlock) {
		if (waypointBlock == null || waypointBlock.trim().isEmpty()) {
			return "";
		}

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
			"Classname\\s*\\(\"([^\"]*)\"\\)\\s*;"
		);
		java.util.regex.Matcher matcher = pattern.matcher(waypointBlock);

		if (matcher.find()) {
			return matcher.group(1).trim();
		}

		return "";
	}

	private String normalizeWaypointInnerBody(String waypointBlock) {
		if (waypointBlock == null) {
			return "";
		}

		String normalized = normalizeWaysRouteBlock(waypointBlock);

		normalized = normalized.replaceFirst("^Waypoint\\s*\\(\\)\\s*\\{\\s*", "");
		normalized = normalized.replaceFirst("^//\\s*\\d+\\s*", "");
		normalized = normalized.replaceFirst("^GotoPos\\s*\\([^\\)]*\\)\\s*;\\s*", "");
		normalized = normalized.replaceFirst("^Classname\\s*\\(\"[^\"]*\"\\)\\s*;\\s*", "");

		if (normalized.endsWith("}")) {
			normalized = normalized.substring(0, normalized.length() - 1).trim();
		}

		return normalized.trim();
	}

	private void appendIndentedWaypointPreview(StringBuilder sb, String waypointBlock, String indent) {
		if (waypointBlock == null || waypointBlock.trim().isEmpty()) {
			return;
		}

		String[] lines = waypointBlock.split("\\r?\\n");
		for (String line : lines) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				sb.append(indent).append("- ").append(trimmed).append("\n");
			}
		}
	}

	private boolean safeEquals(String a, String b) {
		if (a == null) a = "";
		if (b == null) b = "";
		return a.trim().equals(b.trim());
	}

	private int countWaysWaypoints(String routeBlock) {
		if (routeBlock == null || routeBlock.trim().isEmpty()) {
			return 0;
		}

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\bWaypoint\\s*\\(\\)");
		java.util.regex.Matcher matcher = pattern.matcher(routeBlock);

		int count = 0;
		while (matcher.find()) {
			count++;
		}

		return count;
	}

	private java.util.Map<String, String> splitWaysRoutesByIdentifier(String text) {
		java.util.Map<String, String> routes = new java.util.LinkedHashMap<String, String>();

		if (text == null || text.trim().isEmpty()) {
			return routes;
		}

		int index = 0;

		while (true) {
			int routeStart = text.indexOf("Route()", index);
			if (routeStart < 0) {
				break;
			}

			int openBrace = text.indexOf("{", routeStart);
			if (openBrace < 0) {
				break;
			}

			int closeBrace = findMatchingBrace(text, openBrace);
			if (closeBrace < 0) {
				break;
			}

			String routeBlock = text.substring(routeStart, closeBrace + 1).trim();
			String routeId = extractWaysIdentifier(routeBlock);

			if (routeId != null && !routeId.trim().isEmpty()) {
				routes.put(routeId.trim(), routeBlock);
			}

			index = closeBrace + 1;
		}

		return routes;
	}

	private int findMatchingBrace(String text, int openBraceIndex) {
		if (text == null || openBraceIndex < 0 || openBraceIndex >= text.length() || text.charAt(openBraceIndex) != '{') {
			return -1;
		}

		int depth = 0;

		for (int i = openBraceIndex; i < text.length(); i++) {
			char c = text.charAt(i);

			if (c == '{') {
				depth++;
			} else if (c == '}') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
		}

		return -1;
	}

	private String extractWaysIdentifier(String routeBlock) {
		if (routeBlock == null || routeBlock.trim().isEmpty()) {
			return null;
		}

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Identifier\\s*\\(\"([^\"]+)\"\\)");
		java.util.regex.Matcher matcher = pattern.matcher(routeBlock);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private String normalizeWaysRouteBlock(String block) {
		if (block == null) {
			return "";
		}

		String[] lines = block.split("\\r?\\n");
		StringBuilder sb = new StringBuilder();

		for (String line : lines) {
			String normalized = line.trim().replaceAll("\\s+", " ");
			if (!normalized.isEmpty()) {
				sb.append(normalized).append("\n");
			}
		}

		return sb.toString().trim();
	}

	private String normalizeWaysRouteBody(String routeBlock) {
		if (routeBlock == null) {
			return "";
		}

		String normalized = normalizeWaysRouteBlock(routeBlock);

		normalized = normalized.replaceFirst("^Route\\s*\\(\\)\\s*\\{\\s*", "");
		normalized = normalized.replaceFirst("^Identifier\\s*\\(\"[^\"]+\"\\);\\s*", "");

		if (normalized.endsWith("}")) {
			normalized = normalized.substring(0, normalized.length() - 1).trim();
		}

		return normalized.trim();
	}

	private String formatDiffValue(String value) {
		if (value == null || value.trim().isEmpty()) {
			return "(empty)";
		}

		String trimmed = value.trim();

		// Si viene con comillas JSON, las quitamos para que se vea más limpio
		if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
			trimmed = trimmed.substring(1, trimmed.length() - 1);
		}

		return trimmed;
	}

	private java.util.Map<String, java.util.Map<String, String>> parseElemBlocks(String json) {
		java.util.Map<String, java.util.Map<String, String>> result = new java.util.LinkedHashMap<>();

		if (json == null || json.trim().isEmpty()) {
			return result;
		}

		try {
			Pattern blockPattern = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
			Matcher blockMatcher = blockPattern.matcher(json);

			while (blockMatcher.find()) {
				String block = blockMatcher.group();

				Pattern idPattern = Pattern.compile("\"identifier\"\\s*:\\s*\"([^\"]+)\"");
				Matcher idMatcher = idPattern.matcher(block);

				if (!idMatcher.find()) {
					continue;
				}

				String id = idMatcher.group(1);
				java.util.Map<String, String> fields = new java.util.LinkedHashMap<>();

				Pattern fieldPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"[^\"]*\"|\\-?\\d+|true|false|null)");
				Matcher fieldMatcher = fieldPattern.matcher(block);

				while (fieldMatcher.find()) {
					String fieldName = fieldMatcher.group(1);
					String fieldValue = fieldMatcher.group(2);
					fields.put(fieldName, fieldValue);
				}

				result.put(id, fields);
			}
		} catch (Exception e) {
			// ignorar
		}

		return result;
	}

	private void installGlobalUndoRedoInterceptor(Control control) {
		control.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'z' || e.keyCode == 'Z')) {
					undo();
					e.doit = false;
				} else if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'y' || e.keyCode == 'Y')) {
					redo();
					e.doit = false;
				}
			}
		});
	}

	private boolean detectClickedElement(int clickX, int clickY) {
		List<desperados.dvd.elements.Element> elements = FileService.getElements();
		if (elements == null || elements.isEmpty()) {
			return true;
		}
		
		// Rango de detección (radio del círculo es 12, así que 24 total)
		int clickRadius = 15;
		
		for (desperados.dvd.elements.Element elem : elements) {
			int elemX = elem.getX();
			int elemY = elem.getY();
			
			// Calcular distancia euclidiana
			int dx = elemX - clickX;
			int dy = elemY - clickY;
			int distance = (int) Math.sqrt(dx * dx + dy * dy);
			
			if (distance <= clickRadius) {
				// Elemento encontrado
				currentElement = elem;
				FileService.setSelectedElement(elem);
				displayElementInfo(elem);
				updateNavigationButtons();
				navigateToElement(elem);
				canvas.redraw();
				return true;
			}
		}
		
		// No se encontré elemento
		currentElement = null;
		FileService.setSelectedElement(null);
		isRestoringElementInfo = true;
		textElementId.setText("");
		textElementDvf.setText("");
		textElementSprite.setText("");
		textElementX.setText("");
		textElementY.setText("");
		textElementDirection.setText("");
		textElementCharacter.setText("");
		spriteLabel.setImage(null);
		spriteLabel.setText("Click on an element to see its information");
		isRestoringElementInfo = false;
		updateNavigationButtons();
		canvas.redraw();
		return false;
	}

	private void openMapContextMenu(int x, int y) {
		Menu menu = new Menu(shell, SWT.POP_UP);

		MenuItem addEnemy = new MenuItem(menu, SWT.NONE);
		addEnemy.setText("Add Enemy NPC (quick, generic, always Desperado4)");

		addEnemy.addListener(SWT.Selection, e -> {
			addEnemyNpcAt(x, y);
		});

		menu.setLocation(Display.getCurrent().getCursorLocation());
		menu.setVisible(true);
	}

	private void addEnemyNpcAt(int x, int y) {
		String currentText = comboTexts[ScriptItems.ELEM.ordinal()];
		if (currentText == null || currentText.isEmpty()) return;

		int nextId = findNextElementId(currentText);

		String newNpc = buildEnemyNpcJson(nextId, x, y);
		String updated = insertAtEndOfElemArray(currentText, newNpc);

		setComboText(ScriptItems.ELEM.ordinal(), updated);

		String newIdentifier = "Element_" + nextId;

		// Refrescar la vista del elemento recién agregado y seleccionarlo
		selectNewElementFromJson(newIdentifier);
	}

	private desperados.dvd.elements.Element cloneElementObject(
        	desperados.dvd.elements.Element source, String newId) {

		try {
			String text = comboTexts[ScriptItems.ELEM.ordinal()];
			String sourceId = source.getIdentifier();

			String block = extractElementBlock(text, sourceId);
			if (block == null) return null;

			String newBlock = block.replaceFirst(
				"\"identifier\"\\s*:\\s*\"" + Pattern.quote(sourceId) + "\"",
				"\"identifier\" : \"" + newId + "\""
			);

			String updated = insertAtEndOfElemArray(text, newBlock);

			// Para undo, cambios, etc
			setComboText(ScriptItems.ELEM.ordinal(), updated);

			// Recargar elementos
			List<desperados.dvd.elements.Element> elements = FileService.getElements();

			for (desperados.dvd.elements.Element e : elements) {
				if (newId.equals(e.getIdentifier())) {
					return e;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void selectNewElementFromJson(String identifier) {
		try {
			// 🔥 recargar elementos desde JSON actual
			List<desperados.dvd.elements.Element> elements = FileService.getElements();

			for (desperados.dvd.elements.Element elem : elements) {
				if (identifier.equals(elem.getIdentifier())) {

					// guardar como actual
					currentElement = elem;

					displayElementInfo(elem);
					navigateToElement(elem);

					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int findNextElementId(String text) {
		int max = -1;

		java.util.regex.Matcher m = java.util.regex.Pattern
			.compile("\"identifier\"\\s*:\\s*\"Element_(\\d+)\"")
			.matcher(text);

		while (m.find()) {
			int val = Integer.parseInt(m.group(1));
			if (val > max) max = val;
		}

		return max + 1;
	}

	private String buildEnemyNpcJson(int id, int x, int y) {
		return "  {\n" +
			"    \"type\" : \"NPC\",\n" +
			"    \"subtype\" : \"ENEMY\",\n" +
			"    \"identifier\" : \"Element_" + id + "\",\n" +
			"    \"dvf\" : \"Desperado4\",\n" +
			"    \"sprite\" : \"Desperado 04\",\n" +
			"    \"p00\" : 0,\n" +
			"    \"p01\" : 64,\n" +
			"    \"p02\" : 68,\n" +
			"    \"p03\" : 76,\n" +
			"    \"p04\" : 74,\n" +
			"    \"p10\" : 1,\n" +
			"    \"p11\" : 59,\n" +
			"    \"p12\" : 65,\n" +
			"    \"p13\" : 81,\n" +
			"    \"p14\" : 77,\n" +
			"    \"x\" : " + x + ",\n" +
			"    \"y\" : " + y + ",\n" +
			"    \"u1\" : 0,\n" +
			"    \"u2\" : 0,\n" +
			"    \"u3\" : 0,\n" +
			"    \"u4\" : 0,\n" +
			"    \"direction\" : 13,\n" +
			"    \"className\" : \"\",\n" +
			"    \"stance\" : \"STANDING\",\n" +
			"    \"character\" : \"DESPERADO4\",\n" +
			"    \"tiredness\" : 0,\n" +
			"    \"attitude\" : \"HOSTILE\",\n" +
			"    \"drunkLevel\" : 0,\n" +
			"    \"route\" : \"\"\n" +
			"  }";
	}

	private String insertAtEndOfElemArray(String text, String newEntry) {
		int idx = text.lastIndexOf("]");
		if (idx == -1) return text;

		// Ver si ya hay elementos
		boolean hasElements = text.contains("}, {");

		String insert = hasElements ? ",\n" + newEntry : "\n" + newEntry + "\n";

		return text.substring(0, idx) + insert + "\n" + text.substring(idx);
	}

	private void setComboText(int index, String newText) {
		if (index < 0 || index >= comboTexts.length) return;

		comboTexts[index] = newText;

		// ESTE es tu editor real
		if (activeComboItem == index && text != null && !text.isDisposed()) {
			text.setText(newText);
		}

		markCurrentSectionAsChanged();
	}

	private void displayElementInfo(desperados.dvd.elements.Element elem) {
		isRestoringElementInfo = true;
		
		// Llenar los campos de texto con la información del elemento
		textElementId.setText(elem.getIdentifier());
		textElementDvf.setText(elem.getDvf());
		textElementSprite.setText(elem.getSprite());
		textElementX.setText(String.valueOf(elem.getX()));
		textElementY.setText(String.valueOf(elem.getY()));
		
		// Mostrar el campo character si es NPC
		if (elem instanceof desperados.dvd.elements.NPC) {
			desperados.dvd.elements.NPC npc = (desperados.dvd.elements.NPC) elem;
			try {
				java.lang.reflect.Method getter = desperados.dvd.elements.NPC.class.getMethod("getCharacter");
				Object charObj = getter.invoke(npc);
				textElementCharacter.setText(charObj != null ? charObj.toString() : "");
				textElementCharacter.setEditable(true);
			} catch (Exception ex) {
				textElementCharacter.setText("");
				textElementCharacter.setEditable(false);
			}
		} else {
			textElementCharacter.setText("");
			textElementCharacter.setEditable(false);
		}
		
		if (elem instanceof desperados.dvd.elements.Alive) {
			desperados.dvd.elements.Alive alive = (desperados.dvd.elements.Alive) elem;
			textElementDirection.setText(String.valueOf(alive.getDirection()));
			textElementDirection.setEditable(true);
		} else {
			textElementDirection.setText("N/A");
			textElementDirection.setEditable(false);
		}
		
		isRestoringElementInfo = false;
		
		// Asegurar que el composite padre está layouteado antes de mostrar el sprite
		spriteLabel.getParent().layout();
		
		updateSpritePreview(elem);
	}

	private void navigateToElement(desperados.dvd.elements.Element elem) {
		// Asegurarse de que estamos en la sección de ELEM
		if (activeComboItem != ScriptItems.ELEM.ordinal()) {
			combo.select(ScriptItems.ELEM.ordinal());
			combo.notifyListeners(SWT.Selection, new Event());
		}
		
		// Desplazar el mapa para centrar el elemento
		if (scrolledCanvas != null) {
			int elemX = elem.getX();
			int elemY = elem.getY();
			
			// Obtener el tamaño visible del canvas
			org.eclipse.swt.graphics.Rectangle clientArea = scrolledCanvas.getClientArea();
			int visibleWidth = clientArea.width;
			int visibleHeight = clientArea.height;
			
			// Calcular el origen para centrar el elemento
			int originX = Math.max(0, elemX - visibleWidth / 2);
			int originY = Math.max(0, elemY - visibleHeight / 2);
			
			// Asegurarse de no desplazarse más allá del límite
			originX = Math.min(originX, imageWidth - visibleWidth);
			originY = Math.min(originY, imageHeight - visibleHeight);
			
			scrolledCanvas.setOrigin(originX, originY);
		}
		
		// Buscar el identificador en el JSON y seleccionarlo
		String identifier = elem.getIdentifier();
		String jsonContent = text.getText();
		String searchPattern = "\"" + identifier + "\"";
		int searchIndex = jsonContent.indexOf(searchPattern);
		
		if (searchIndex >= 0) {
			// Calcular línea y columna
			int lineNumber = jsonContent.substring(0, searchIndex).split("\n").length - 1;
			
			// Ir a esa línea
			try {
				// Seleccionar el identificador (sin las comillas)
				int startSelection = searchIndex + 1; // Después de la comilla inicial
				int endSelection = startSelection + identifier.length();
				
				text.setCaretOffset(startSelection);
				text.setSelection(startSelection, endSelection);
				
				// Scroll para mostrar la línea
				text.setTopIndex(Math.max(0, lineNumber - 5));
				text.showSelection();
			} catch (Exception e) {
				// Ignorar errores
			}
		}
	}

	private void updateNavigationButtons() {
		if (currentElement == null) {
			prevElementButton.setEnabled(false);
			nextElementButton.setEnabled(false);
			return;
		}
		
		List<desperados.dvd.elements.Element> elements = FileService.getElements();
		if (elements == null) {
			prevElementButton.setEnabled(false);
			nextElementButton.setEnabled(false);
			return;
		}
		
		String currentId = currentElement.getIdentifier();
		int indexUnderscore = currentId.lastIndexOf("_");
		
		if (indexUnderscore <= 0) {
			prevElementButton.setEnabled(false);
			nextElementButton.setEnabled(false);
			return;
		}
		
		try {
			String prefix = currentId.substring(0, indexUnderscore + 1);
			int currentNum = Integer.parseInt(currentId.substring(indexUnderscore + 1));
			
			int prevNum = currentNum - 1;
			int nextNum = currentNum + 1;
			
			String prevId = prefix + prevNum;
			String nextId = prefix + nextNum;
			
			boolean hasPrev = false;
			boolean hasNext = false;
			
			for (desperados.dvd.elements.Element elem : elements) {
				if (elem.getIdentifier().equals(prevId)) {
					hasPrev = true;
				}
				if (elem.getIdentifier().equals(nextId)) {
					hasNext = true;
				}
			}
			
			prevElementButton.setEnabled(hasPrev);
			nextElementButton.setEnabled(hasNext);
		} catch (NumberFormatException e) {
			prevElementButton.setEnabled(false);
			nextElementButton.setEnabled(false);
		}
	}

	private void previousElement() {
		if (currentElement == null) {
			return;
		}
		
		List<desperados.dvd.elements.Element> elements = FileService.getElements();
		if (elements == null) {
			return;
		}
		
		String currentId = currentElement.getIdentifier();
		int indexUnderscore = currentId.lastIndexOf("_");
		
		if (indexUnderscore <= 0) {
			return;
		}
		
		try {
			String prefix = currentId.substring(0, indexUnderscore + 1);
			int currentNum = Integer.parseInt(currentId.substring(indexUnderscore + 1));
			int prevNum = currentNum - 1;
			String prevId = prefix + prevNum;
			
			for (desperados.dvd.elements.Element elem : elements) {
				if (elem.getIdentifier().equals(prevId)) {
					currentElement = elem;
					FileService.setSelectedElement(elem);
					displayElementInfo(elem);
					updateNavigationButtons();
					navigateToElement(elem);
					canvas.redraw();
					return;
				}
			}
		} catch (NumberFormatException e) {
			// Ignorar errores
		}
	}

	private void nextElement() {
		if (currentElement == null) {
			return;
		}
		
		List<desperados.dvd.elements.Element> elements = FileService.getElements();
		if (elements == null) {
			return;
		}
		
		String currentId = currentElement.getIdentifier();
		int indexUnderscore = currentId.lastIndexOf("_");
		
		if (indexUnderscore <= 0) {
			return;
		}
		
		try {
			String prefix = currentId.substring(0, indexUnderscore + 1);
			int currentNum = Integer.parseInt(currentId.substring(indexUnderscore + 1));
			int nextNum = currentNum + 1;
			String nextId = prefix + nextNum;
			
			for (desperados.dvd.elements.Element elem : elements) {
				if (elem.getIdentifier().equals(nextId)) {
					currentElement = elem;
					FileService.setSelectedElement(elem);
					displayElementInfo(elem);
					updateNavigationButtons();
					navigateToElement(elem);
					canvas.redraw();
					return;
				}
			}
		} catch (NumberFormatException e) {
			// Ignorar errores
		}
	}

	private void updateSpritePreview(desperados.dvd.elements.Element elem) {
		// Limpiar completamente primero
		spriteLabel.setImage(null);
		spriteLabel.setText("");
		
		// Llamar en el siguiente ciclo del event loop
		org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (spriteLabel.isDisposed()) {
					return;
				}
				
				// Obtener la imagen del sprite
				org.eclipse.swt.graphics.Image spriteImage = FileService.getElementSpriteImage(elem);
				
				if (spriteImage != null) {
					spriteLabel.setImage(spriteImage);
					spriteLabel.setText("");
				} else {
					spriteLabel.setImage(null);
					spriteLabel.setText("No sprite preview available");
				}
			}
		});
	}

	private void regenerateJSON() {
		if (activeComboItem == ScriptItems.ELEM.ordinal()) {
			List<desperados.dvd.elements.Element> elements = FileService.getElements();
			if (elements != null) {
				String currentElementId = (currentElement != null) ? currentElement.getIdentifier() : null;

				String newJSON = desperados.util.ElementsJsonWriter.writeToString(elements);

				// Guardar posición actual del JSON
				int caret = 0;
				int topIndex = 0;
				try {
					caret = text.getCaretOffset();
					topIndex = text.getTopIndex();
				} catch (Exception ex) {
					// ignorar
				}

				isRestoring = true;
				text.setText(newJSON);
				comboTexts[activeComboItem] = newJSON;
				originalComboTexts[activeComboItem] = newJSON;

				// Restaurar posición del JSON
				try {
					if (caret >= 0 && caret <= text.getCharCount()) {
						text.setCaretOffset(caret);
					}
				} catch (Exception ex) {
					// ignorar
				}

				try {
					text.setTopIndex(topIndex);
				} catch (Exception ex) {
					// ignorar
				}

				isRestoring = false;

				// volver a parsear desde el JSON recién regenerado para que FileService y currentElement queden siempre alineados
				try {
					FileService.readElementsFromString(newJSON);

					if (currentElementId != null) {
						List<desperados.dvd.elements.Element> refreshed = FileService.getElements();
						for (desperados.dvd.elements.Element elem : refreshed) {
							if (elem.getIdentifier().equals(currentElementId)) {
								currentElement = elem;
								FileService.setSelectedElement(elem);
								break;
							}
						}
					}
				} catch (ServiceException e) {
					// ignorar
				}

				if (currentElement != null) {
					if (!isUpdatingFromPanel) {
						// Si el cambio vino desde JSON o restore, refrescar panel + navegar
						isRestoringElementInfo = true;
						displayElementInfo(currentElement);
						isRestoringElementInfo = false;
					}

					// Siempre mantener el JSON enfocado en el elemento actual
					navigateToElement(currentElement);
				}
			}
		}
	}

	private void updatePanelFromJSON() {
		// Actualizar los campos del panel en tiempo real mientras se edita el JSON
		if (currentElement == null) {
			return;
		}

		try {
			// 1) Reconstruir la lista de elementos desde el JSON actual
			FileService.readElementsFromString(text.getText());

			// 2) Reencontrar currentElement por identifier
			String currentElementId = currentElement.getIdentifier();
			List<desperados.dvd.elements.Element> elements = FileService.getElements();

			if (elements == null) {
				return;
			}

			desperados.dvd.elements.Element updatedElement = null;
			for (desperados.dvd.elements.Element elem : elements) {
				if (elem.getIdentifier().equals(currentElementId)) {
					updatedElement = elem;
					break;
				}
			}

			if (updatedElement == null) {
				return;
			}

			// 3) Reemplazar la referencia vieja por la nueva
			currentElement = updatedElement;
			FileService.setSelectedElement(updatedElement);

			// 4) Refrescar todo el panel desde el objeto real actualizado
			displayElementInfo(updatedElement);

		} catch (Exception e) {
			// Ignorar errores mientras el usuario está escribiendo JSON incompleto
		}
	}
}