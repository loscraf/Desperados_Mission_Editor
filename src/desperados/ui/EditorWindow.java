package desperados.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
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

	public static String gameDir;
	public static String exeName;

	private final static String appName = "Desperados Mission Editor";
	private final static String appVersion = "v0.84";

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
	
	private StyledText text;
	private StyledText textConsole;
	private StyledText textCoords;
	private StyledText elementInfo;
	private Text searchText;
	private String[] originalComboTexts;
	private Combo combo;
	private desperados.dvd.elements.Element currentElement;
	private desperados.dvd.elements.Element selectedElement;
	private Button prevElementButton;
	private Button nextElementButton;
	private ScrolledComposite scrolledCanvas;
	
	private ArrayList<String[]> historyStack;
	private ArrayList<Integer> historyCaret;
	private ArrayList<Integer> historyScroll;
	private ArrayList<Integer> historyActiveItem;
	private int historyIndex;
	private static final int MAX_HISTORY = 1000;
	private Button undoButton;
	private Button redoButton;
	private boolean isRestoring = false;
	private boolean isFirstChange = true;
	
	private int activeComboItem;
	private String[] comboItems;
	private String[] comboTexts;
	private int[] textPositions;

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
		for (int i = 0; i < comboTexts.length; i++) {
			comboTexts[i] = "TODO";
			originalComboTexts[i] = "TODO";
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
		
		dvdLoaded = true;
		
		shell.dispose();
		run();
	}

	private void loadElementText() {
		String text = FileService.getElementText();
		comboTexts[ScriptItems.ELEM.ordinal()] = text;
		originalComboTexts[ScriptItems.ELEM.ordinal()] = text;
	}

	private void loadScriptText() {
		String text = FileService.readScbFile();
		comboTexts[ScriptItems.SCB.ordinal()] = text;
		originalComboTexts[ScriptItems.SCB.ordinal()] = text;
	}

	private void loadLocationsText() {
		String text = FileService.getLocationText();
		comboTexts[ScriptItems.SCRP.ordinal()] = text;
		originalComboTexts[ScriptItems.SCRP.ordinal()] = text;
	}

	private void loadBuildingsText() {
		String text = FileService.getBuildingsText();
		comboTexts[ScriptItems.BUIL.ordinal()] = text;
		originalComboTexts[ScriptItems.BUIL.ordinal()] = text;
	}

	private void loadWaypointText() {
		List<WaypointRoute> routes = FileService.getWaypointRoutes();
		if (routes != null) {
			String str = "";
			for (WaypointRoute r : routes) {
				str += r.toString() + "\n";
			}
			comboTexts[ScriptItems.WAYS.ordinal()] = str;
			originalComboTexts[ScriptItems.WAYS.ordinal()] = str;
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
		lbl.setText("Enter level number and press enter:");
		
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
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		shell.setLayout(gridLayout);
		
		ScrolledComposite sc = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Thread updateThread = new Thread() {
	        public void run() {
	            while (true) {
	                display.syncExec(new Runnable() {
	                    @Override
	                    public void run() {
	                    	if ((drawAnimations || drawElements) && !canvas.isDisposed()) {
	                    		canvas.redraw();
	                    	}
	                    }
	                });
	                try {
	                    Thread.sleep(200);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    };
	    updateThread.setDaemon(true);
	    updateThread.start();
		
		canvas = new Canvas(sc, SWT.DOUBLE_BUFFERED);
		
		canvas.addListener(SWT.MouseUp, new Listener(){
	        public void handleEvent(Event e){
	        	String coordText = (e.x) + "," + (e.y);
	        	textCoords.setText(coordText);
	        	copyToClipboard(coordText);
	        	
	        	// Detectar si hizo click en algún elemento
	        	if (drawElements || drawAnimations) {
	        		detectClickedElement(e.x, e.y);
	        	}
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
		
		Composite contentComposite = new Composite(shell, SWT.BORDER);
		contentComposite.setLayout(new GridLayout());
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		sc.setLayoutData(gridData);
		scrolledCanvas = sc;
		
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumWidth = 500;
		contentComposite.setLayoutData(gridData);
		
		Composite undoRedoComposite = new Composite(contentComposite, SWT.NONE);
		GridLayout undoRedoLayout = new GridLayout();
		undoRedoLayout.numColumns = 2;
		undoRedoComposite.setLayout(undoRedoLayout);
		GridData undoRedoData = new GridData(GridData.FILL_HORIZONTAL);
		undoRedoComposite.setLayoutData(undoRedoData);
		
		undoButton = new Button(undoRedoComposite, SWT.NONE);
		undoButton.setText("Deshacer (Ctrl+Z)");
		undoButton.setEnabled(false);
		undoButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            undo();
	        }
	    });
		
		redoButton = new Button(undoRedoComposite, SWT.NONE);
		redoButton.setText("Rehacer (Ctrl+Y)");
		redoButton.setEnabled(false);
		redoButton.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            redo();
	        }
	    });
		
		Button checkBoxElements = new Button(contentComposite, SWT.CHECK);
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
		
	    Button checkBoxAnimations = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxIdentifier = new Button(contentComposite, SWT.CHECK);
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
	    
		Button checkBoxObstacles = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxWaypoints = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxAI = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxLocations = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxDoors = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxMaterials = new Button(contentComposite, SWT.CHECK);
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
	    
	    Button checkBoxCoords = new Button(contentComposite, SWT.CHECK);
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
	    
	    Label searchLabel = new Label(contentComposite, SWT.NONE);
	    searchLabel.setText("Search:");
	    
	    searchText = new Text(contentComposite, SWT.BORDER);
	    searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    searchText.addModifyListener(new ModifyListener() {
	        @Override
	        public void modifyText(ModifyEvent e) {
	            String searchTerm = searchText.getText().toLowerCase();
	            applySearchFilter(searchTerm);
	        }
	    });
	    
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
		
		text.addKeyListener(new KeyAdapter() {
		    @Override
		    public void keyPressed(KeyEvent e) {
		    	if (e.stateMask == SWT.CTRL && e.keyCode == 'z') {
		    		undo();
		    		e.doit = false;
		    	} else if (e.stateMask == SWT.CTRL && e.keyCode == 'y') {
		    		redo();
		    		e.doit = false;
		    	}
		        if (e.stateMask == SWT.CTRL && e.keyCode == 'a') {
		            text.selectAll();
		            e.doit = false;
		        }
		        setConsoleText("");
		    }
		});
		
		textConsole = new StyledText(contentComposite, SWT.BORDER | SWT.H_SCROLL);
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
		textCoords.setFont(new Font(display, new FontData("Courier New", 10, SWT.NORMAL)));
		textCoords.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label infoLabel = new Label(coordsComposite, SWT.NONE);
		infoLabel.setText("Element Info:");
		
		elementInfo = new StyledText(coordsComposite, SWT.BORDER | SWT.READ_ONLY);
		elementInfo.setFont(new Font(display, new FontData("Courier New", 9, SWT.NORMAL)));
		elementInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		elementInfo.setText("Click on an element to see its information");
		
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
		
	    Button buttonUpdate = new Button(contentComposite, SWT.NONE);
	    buttonUpdate.setText("Write Current Section To File");
	    buttonUpdate.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	        	setConsoleText("");
	        	
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
	        }
	    });
	}

	private void copyToClipboard(String string) {
		TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new Object[] { string }, new Transfer[] { textTransfer });
	}

	public void setConsoleText(String text) {
		textConsole.setText(text);
	}

	public void setElementInfo(String info) {
		elementInfo.setText(info);
	}

	private void writeElementsToDvd() {
		try {
			FileService.writeElementsFromStringToDvd(text.getText());
			setConsoleText("Writing ELEM section to DVD completed!");
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
	}

	private void applySearchFilter(String searchTerm) {
		if (searchTerm.isEmpty()) {
			isRestoring = true;
			text.setText(originalComboTexts[activeComboItem]);
			isRestoring = false;
			comboTexts[activeComboItem] = originalComboTexts[activeComboItem];
			return;
		}
		
		String original = originalComboTexts[activeComboItem];
		String[] lines = original.split("\n");
		StringBuilder filtered = new StringBuilder();
		
		for (String line : lines) {
			if (line.toLowerCase().contains(searchTerm)) {
				filtered.append(line).append("\n");
			}
		}
		
		String result = filtered.toString();
		if (result.endsWith("\n")) {
			result = result.substring(0, result.length() - 1);
		}
		
		isRestoring = true;
		text.setText(result);
		isRestoring = false;
		comboTexts[activeComboItem] = result;
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
	}

	private void updateUndoRedoButtons() {
		if (undoButton != null) {
			undoButton.setEnabled(historyIndex > 0);
		}
		if (redoButton != null) {
			redoButton.setEnabled(historyIndex < historyStack.size() - 1);
		}
	}

	private void detectClickedElement(int clickX, int clickY) {
		List<desperados.dvd.elements.Element> elements = FileService.getElements();
		if (elements == null || elements.isEmpty()) {
			return;
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
				selectedElement = elem;
				FileService.setSelectedElement(elem);
				displayElementInfo(elem);
				updateNavigationButtons();
				navigateToElement(elem);
				canvas.redraw();
				return;
			}
		}
		
		// No se encontró elemento
		currentElement = null;
		selectedElement = null;
		FileService.setSelectedElement(null);
		setElementInfo("No element at this location");
		updateNavigationButtons();
		canvas.redraw();
		canvas.redraw();
	}

	private void displayElementInfo(desperados.dvd.elements.Element elem) {
		StringBuilder info = new StringBuilder();
		info.append("ID: ").append(elem.getIdentifier()).append("\n");
		info.append("X: ").append(elem.getX()).append("\n");
		info.append("Y: ").append(elem.getY()).append("\n");
		info.append("Sprite: ").append(elem.getSprite()).append("\n");
		
		if (elem instanceof desperados.dvd.elements.Alive) {
			desperados.dvd.elements.Alive alive = (desperados.dvd.elements.Alive) elem;
			info.append("Direction: ").append(alive.getDirection()).append("\n");
		}
		
		setElementInfo(info.toString());
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
					selectedElement = elem;
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
					selectedElement = elem;
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
}